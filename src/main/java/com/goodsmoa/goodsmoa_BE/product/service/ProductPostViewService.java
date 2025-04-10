package com.goodsmoa.goodsmoa_BE.product.service;

import com.goodsmoa.goodsmoa_BE.product.repository.ProductPostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductPostViewService {

    private static final String VIEW_COUNT_KEY = "productPost:view:";

    private final RedisTemplate<String, Object> redisTemplate;
    private final ProductPostRepository productPostRepository;

    /** ✅ 조회수 증가 (Cluster에서도 동일하게 작동) */
    public void increaseViewCount(Long postId) {
        String redisKey = VIEW_COUNT_KEY + postId;
        redisTemplate.opsForValue().increment(redisKey);
    }

    /** ✅ Redis Cluster에서도 작동 가능한 방식으로 조회수 동기화 */
    @Scheduled(cron = "0 */1 * * * *")
    @Transactional
    public void syncViewCountToDatabase() {
        log.info("조회수 동기화 시작");

        Set<String> keys = scanKeys(VIEW_COUNT_KEY + "*");

        if (keys.isEmpty()) {
            return;
        }

        for (String key : keys) {
            Long postId = Long.parseLong(key.replace(VIEW_COUNT_KEY, ""));
            Object redisValue = redisTemplate.opsForValue().get(key);
            Long views = redisValue != null ? Long.parseLong(redisValue.toString()) : 0L;

            productPostRepository.findById(postId).ifPresent(post -> {
                post.getViews(views);  // 이 부분이 setView 같음. 검토 필요
                productPostRepository.save(post);
            });

            redisTemplate.delete(key);
        }
    }

    /** ✅ SCAN 방식으로 Redis Cluster에서 키 가져오기 */
    private Set<String> scanKeys(String pattern) {
        return redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
            Set<String> keys = new HashSet<>();
            ScanOptions options = ScanOptions.scanOptions().match(pattern).count(1000).build();

            try (var cursor = connection.scan(options)) {
                while (cursor.hasNext()) {
                    byte[] keyBytes = cursor.next();
                    keys.add(new String(keyBytes, StandardCharsets.UTF_8));
                }
            } catch (Exception e) {
                log.error("Redis scan error: ", e);
            }

            return keys;
        });
    }
}
