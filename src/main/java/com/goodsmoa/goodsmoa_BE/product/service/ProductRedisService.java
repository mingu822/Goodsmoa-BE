package com.goodsmoa.goodsmoa_BE.product.service;

import com.goodsmoa.goodsmoa_BE.product.entity.ProductPostEntity;
import com.goodsmoa.goodsmoa_BE.product.repository.ProductPostRepository;
import com.goodsmoa.goodsmoa_BE.search.converter.SearchConverter;
import com.goodsmoa.goodsmoa_BE.search.document.SearchDocument;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexedObjectInformation;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductRedisService {

    private static final String VIEW_COUNT_KEY = "productPost:view:";
    private static final String LIKE_COUNT_KEY = "productPost:like:";

    private final RedisTemplate<String, Object> redisTemplate;
    private final ProductPostRepository productPostRepository;
    private final SearchConverter searchConverter;
    private final ElasticsearchOperations elasticsearchOperations;

    private final Set<Long> updatedPostIds = ConcurrentHashMap.newKeySet();

    public void increaseViewCount(Long postId) {
        String key = VIEW_COUNT_KEY + postId;
        redisTemplate.opsForValue().increment(key);
        log.info("상품 게시물 조회수 증가: {} -> {}", postId, getLongFromRedis(key));
    }

    public void increaseLikeCount(Long postId) {
        String key = LIKE_COUNT_KEY + postId;
        redisTemplate.opsForValue().increment(key);
        log.info("상품 게시물 좋아요 증가.레디스에 있는 좋아요수: {}",getLongFromRedis(key));
    }

    public void decreaseLikeCount(Long postId) {
        String key = LIKE_COUNT_KEY + postId;
        redisTemplate.opsForValue().decrement(key);
        log.info("상품 게시물 좋아요 감소: {}",getLongFromRedis(key));
    }

    @Scheduled(cron = "0 */1 * * * *")
    @Transactional
    public void syncStatsToDatabase() {
        try {
            syncViewCounts();
            syncLikeCounts();
            reindexUpdatedPosts();
        } catch (Exception e) {
            log.error("상품 게시글 통계 동기화 실패", e);
        }
    }

    public void syncViewCounts() {
        log.info("상품 게시글 조회수 동기화 시작");
        Set<String> keys = scanKeys(redisTemplate, VIEW_COUNT_KEY + "*");

        for (String key : keys) {
            Long postId = Long.parseLong(key.replace(VIEW_COUNT_KEY, ""));
            Long views = getLongFromRedis(key);

            productPostRepository.findById(postId).ifPresent(post -> {
                if (views > 0) {
                    post.getViews(views);  // 누적
                    updatedPostIds.add(postId);
                }
            });
            redisTemplate.delete(key);
        }
    }

    public void syncLikeCounts() {
        log.info("상품 게시글 좋아요수 동기화 시작");
        Set<String> keys = scanKeys(redisTemplate, LIKE_COUNT_KEY + "*");

        for (String key : keys) {
            Long postId = Long.parseLong(key.replace(LIKE_COUNT_KEY, ""));
            Long likes = getLongFromRedis(key);

            productPostRepository.findById(postId).ifPresent(post -> {
                if (likes != 0) {
                    post.setLikes(post.getLikes() + likes);
                    updatedPostIds.add(postId);
                }
            });
            redisTemplate.delete(key);
        }
    }

    private void reindexUpdatedPosts() {
        if (updatedPostIds.isEmpty()) return;

        log.info("ES 재색인 대상: {}", updatedPostIds);

        List<ProductPostEntity> posts = productPostRepository.findAllById(updatedPostIds);
        productPostRepository.flush();

        List<IndexQuery> indexQueries = posts.stream()
                .map(post -> {
                    SearchDocument doc = searchConverter.toDocument(post);
                    log.info("색인 준비 - ID: {}, views: {}", doc.getId(), doc.getViews());
                    return new IndexQueryBuilder()
                            .withId("PRODUCT_" + post.getId())
                            .withObject(doc)
                            .build();
                }).toList();

        try {
            List<IndexedObjectInformation> results = elasticsearchOperations.bulkIndex(
                    indexQueries,
                    IndexCoordinates.of("search_document")
            );
            for (IndexedObjectInformation info : results) {
                log.info("ES 색인 완료 ");
            }
        } catch (Exception e) {
            log.error("ES 색인 실패", e);
        } finally {
            updatedPostIds.clear();
        }
    }



    private Long getLongFromRedis(String key) {
        Object value = redisTemplate.opsForValue().get(key);
        return value != null ? Long.parseLong(value.toString()) : 0L;
    }

    public static Set<String> scanKeys(RedisTemplate<String, ?> redisTemplate, String pattern) {
        return redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
            Set<String> keys = new HashSet<>();
            ScanOptions options = ScanOptions.scanOptions().match(pattern).count(1000).build();
            try (var cursor = connection.scan(options)) {
                while (cursor.hasNext()) {
                    keys.add(new String(cursor.next(), StandardCharsets.UTF_8));
                }
            } catch (Exception e) {
                log.error("Redis scan error for pattern {}: {}", pattern, e.getMessage());
            }
            return keys;
        });
    }
}
