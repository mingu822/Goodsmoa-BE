package com.goodsmoa.goodsmoa_BE.demand.service;

import com.goodsmoa.goodsmoa_BE.demand.entity.DemandPostEntity;
import com.goodsmoa.goodsmoa_BE.demand.repository.DemandPostProductRepository;
import com.goodsmoa.goodsmoa_BE.demand.repository.DemandPostRepository;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class DemandRedisService {
    private static final String VIEW_COUNT_KEY = "demandPost:view:";
    private static final String LIKE_COUNT_KEY = "demandPost:like:";
    private static final String DEMAND_ORDER_KEY = "demandPostProduct:orderCount";

    private final RedisTemplate<String, Object> redisTemplate;
    private final DemandPostRepository demandPostRepository;
    private final DemandPostProductRepository demandPostProductRepository;
    private final SearchConverter searchConverter;
    private final ElasticsearchOperations elasticsearchOperations;

    private final Set<Long> updatedPostIds = ConcurrentHashMap.newKeySet();

    // 조회수 증가
    public void increaseViewCount(Long demandId){
        String redisKey = VIEW_COUNT_KEY + demandId;
        redisTemplate.opsForValue().increment(redisKey);
        Long after = getLongFromRedis(redisKey);
        log.info("조회수 증가: 게시물 ID={}, 증감값={}", demandId, after);
    }

    // 좋아요 증가
    public void increaseLikeCount(Long demandId) {
        String redisKey = LIKE_COUNT_KEY + demandId;
        redisTemplate.opsForValue().increment(redisKey);
        Long after = getLongFromRedis(redisKey);
        log.info("좋아요 증가: 게시물 ID={}, 증감값={}", demandId, after);
    }

    // 좋아요 감소
    public void decreaseLikeCount(Long demandId) {
        String redisKey = LIKE_COUNT_KEY + demandId;
        redisTemplate.opsForValue().decrement(redisKey);
        Long after = getLongFromRedis(redisKey);
        log.info("좋아요 감소: 게시물 ID={}, 증감값={}", demandId, after);
    }

    //
    public void increaseOrderCount(Long demandProductId, int quantity){
        String redisKey = DEMAND_ORDER_KEY + demandProductId;
        redisTemplate.opsForValue().increment(redisKey, quantity);
    }

    public void decreaseOrderCount(Long demandProductId, int quantity){
        String redisKey = DEMAND_ORDER_KEY + demandProductId;
        redisTemplate.opsForValue().decrement(redisKey, quantity);
    }

    @Scheduled(cron = "0 */1 * * * *")
    @Transactional
    public void syncStatsToDatabase() {
        try {
            syncViewCounts(); //조회수 동기화
            syncLikeCounts(); //좋아요수 동기화
            syncRateCounts(); //참여도 동기화
            reindexUpdatedPosts(); //수정된 게시물(조회수,좋아요) ES 재색인
        } catch (Exception e) {
            log.error("수요조사 좋아요 스케줄 실패", e);
        }
    }

    public void syncViewCounts(){
        log.info("수요조사 조회수 동기화 시작합니다");
        Set<String> keys = scanKeys(redisTemplate, VIEW_COUNT_KEY + "*");

        if(keys.isEmpty()){
            return;
        }
        for (String key : keys) {
            Long demandId = Long.parseLong(key.replace(VIEW_COUNT_KEY, ""));
            Long views = getLongFromRedis(key);

            demandPostRepository.findById(demandId).ifPresent(post -> {
                if (views > 0) {
                    post.setViews(post.getViews()+views); // DB 업데이트
                    updatedPostIds.add(demandId); // ES 재색인 대상 추가
                }
            });
            redisTemplate.delete(key);
        }
    }

    private void syncLikeCounts() {
        log.info("수요조사 좋아요수 동기화 시작합니다");
        Set<String> likeKeys = scanKeys(redisTemplate, LIKE_COUNT_KEY + "*");
        for (String key : likeKeys) {
            Long demandId = Long.parseLong(key.replace(LIKE_COUNT_KEY, ""));
            Long likes = getLongFromRedis(key);

            demandPostRepository.findById(demandId).ifPresent(post -> {
                if (likes != 0) {
                    post.setLikes(post.getLikes()+likes); // DB 업데이트
                    updatedPostIds.add(demandId); // ES 재색인 대상 추가
                }
            });
            redisTemplate.delete(key);
        }
    }

    public void syncRateCounts() {
        log.info("수요조사 달성율 동기화 시작합니다");
        Set<String> keys = scanKeys(redisTemplate, DEMAND_ORDER_KEY + "*");

        if(keys.isEmpty()) return;
        for (String key : keys) {
            Long demandProductId = Long.parseLong(key.replace(DEMAND_ORDER_KEY, ""));
            Object redisValue = redisTemplate.opsForValue().get(key);
            int orderCount = redisValue != null ? Integer.parseInt(redisValue.toString()) : 0;

            demandPostProductRepository.findById(demandProductId).ifPresent(demandPostProduct -> {
                int currentCount = demandPostProduct.getOrderCount();
                int diff = currentCount+orderCount;
                demandPostProduct.setOrderCount(diff);
            });
            redisTemplate.delete(key);
        }
    }


    private Long getLongFromRedis(String key) {
        Object value = redisTemplate.opsForValue().get(key);
        return value != null ? Long.parseLong(value.toString()) : 0L;
    }

    private void reindexUpdatedPosts() {
        if (updatedPostIds.isEmpty()) return;

        log.info("ES 재색인 대상: {}", updatedPostIds);

        List<DemandPostEntity> posts = demandPostRepository.findAllById(updatedPostIds);

        demandPostRepository.flush();

        List<IndexQuery> indexQueries = posts.stream()
                .map(post -> {
                    SearchDocument doc = searchConverter.toDocument(post);
                    log.info("색인 준비 - ID: {}, views: {}, likes: {}", doc.getId(), doc.getViews(), doc.getLikes());
                    return new IndexQueryBuilder()
                            .withId("DEMAND_" + post.getId())
                            .withObject(doc)
                            .build();
                })
                .toList();

        try {
            List<IndexedObjectInformation> results = elasticsearchOperations.bulkIndex(
                    indexQueries,
                    IndexCoordinates.of("search_document")
            );

            for (int i = 0; i < results.size(); i++) {
                String id = indexQueries.get(i).getId();
                log.info("ES 색인 완료 - 문서 ID: {}", id);
            }

        } catch (Exception e) {
            log.error("ES 재색인 실패", e);
        } finally {
            updatedPostIds.clear();
        }
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
