package com.goodsmoa.goodsmoa_BE.community.scheduler;

import com.goodsmoa.goodsmoa_BE.community.entity.CommunityPostEntity;
import com.goodsmoa.goodsmoa_BE.community.repository.CommunityPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommunityViewSyncScheduler {

    private final StringRedisTemplate redisTemplate;
    private final CommunityPostRepository postRepository;

    // 1분마다 실행 (매 분 0초)
    @Scheduled(cron = "0 * * * * *")
    public void syncViewsToDB() {
        // communityPost:view:*  로 시작하는 모든 키 조회
        Set<String> keys = redisTemplate.keys("communityPost:view:*");

        if (keys == null || keys.isEmpty()) return;

        for (String key : keys) {
            try {
                // post:view:10 → 10 만 추출 (게시글 ID)
                Long postId = Long.parseLong(key.replace("communityPost:view:", ""));
                String value = redisTemplate.opsForValue().get(key);

                if (value == null) continue;

                Long viewsFromRedis = Long.parseLong(value);


                CommunityPostEntity post = postRepository.findById(postId).orElse(null);
                if (post == null) continue;

                //  DB에 누적 조회수 반영 (기존 조회수 + Redis 값)
                post.incraseViews(viewsFromRedis);

                postRepository.save(post);

                // Redis 값 삭제 (중복 반영 방지 + 메모리 절약 목적!)
                redisTemplate.delete(key);

                log.info("DB에 조회수 반영됨(비동기 저장 성공) - postId: {} | Redis 증가분: {} | → 반영 후: {}",
                        postId, viewsFromRedis, post.getViews());

            } catch (Exception e) {
                log.error(" 조회수 동기화 중 에러 발생: {}", e.getMessage());
            }
        }
    }
}
