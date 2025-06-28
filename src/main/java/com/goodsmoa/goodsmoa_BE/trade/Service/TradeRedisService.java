package com.goodsmoa.goodsmoa_BE.trade.Service;


import com.goodsmoa.goodsmoa_BE.search.converter.SearchConverter;
import com.goodsmoa.goodsmoa_BE.search.document.SearchDocument;
import com.goodsmoa.goodsmoa_BE.trade.Entity.TradePostEntity;;
import com.goodsmoa.goodsmoa_BE.trade.Repository.TradePostRepository;
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
public class TradeRedisService {
    private static final String VIEW_COUNT_KEY = "tradePost:view:";
    private static final String LIKE_COUNT_KEY = "tradePost:like:";

    private final RedisTemplate<String, Object> redisTemplate;
    private final TradePostRepository tradePostRepository;
    private final SearchConverter searchConverter;
    private final ElasticsearchOperations elasticsearchOperations;


    private final Set<Long> updatedPostIds = ConcurrentHashMap.newKeySet();

    public void increaseViewCount(Long postId) {
        String key = VIEW_COUNT_KEY + postId;
        redisTemplate.opsForValue().increment(key);
        log.info("거래 게시물 조회수 증가: {}",getLongFromRedis(key));
    }

    public void increaseLikeCount(Long postId) {
        String key = LIKE_COUNT_KEY + postId;
        redisTemplate.opsForValue().increment(key);
        log.info("거래 게시물 좋아요 증가: {}", getLongFromRedis(key));
    }

    public void decreaseLikeCount(Long postId) {
        String key = LIKE_COUNT_KEY + postId;
        redisTemplate.opsForValue().decrement(key);
        log.info("거래 게시물 좋아요 감소: {}", getLongFromRedis(key));
    }

    @Scheduled(cron = "0 */1 * * * *")
    @Transactional
    public void syncStatsToDatabase() {
        try {
            syncViewCounts();
            syncLikeCounts();
            reindexUpdatedPosts();
        } catch (Exception e) {
            log.error("거래 게시글 통계 동기화 실패", e);
        }
    }

    public void syncViewCounts() {
        log.info("거래 게시글 조회수 동기화 시작");
        Set<String> keys = scanKeys(VIEW_COUNT_KEY + "*");

        for (String key : keys) {
            Long postId = Long.parseLong(key.replace(VIEW_COUNT_KEY, ""));
            Long views = getLongFromRedis(key);

            tradePostRepository.findById(postId).ifPresent(post -> {
                if (views > 0) {
                    post.getViews(views);
                    updatedPostIds.add(postId);
                }
            });

            redisTemplate.delete(key);
        }
    }

    public void syncLikeCounts() {
        log.info("거래 게시글 좋아요수 동기화 시작");
        Set<String> keys = scanKeys(LIKE_COUNT_KEY + "*");

        for (String key : keys) {
            Long postId = Long.parseLong(key.replace(LIKE_COUNT_KEY, ""));
            Long likes = getLongFromRedis(key);

            tradePostRepository.findById(postId).ifPresent(post -> {
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
        List<TradePostEntity> posts = tradePostRepository.findAllById(updatedPostIds);
        tradePostRepository.flush();

        List<IndexQuery> indexQueries = posts.stream()
                .map(post -> {
                    SearchDocument doc = searchConverter.toDocument(post);
                    return new IndexQueryBuilder()
                            .withId("TRADE_" + post.getId())
                            .withObject(doc)
                            .build();
                }).toList();

        try {
            List<IndexedObjectInformation> results = elasticsearchOperations.bulkIndex(
                    indexQueries, IndexCoordinates.of("search_document")
            );
            results.forEach(result -> log.info("ES 색인 완료: {}"));
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

    private Set<String> scanKeys(String pattern) {
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
