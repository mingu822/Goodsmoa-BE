package com.goodsmoa.goodsmoa_BE.demand.service;

import com.goodsmoa.goodsmoa_BE.demand.entity.DemandLikeEntity;
import com.goodsmoa.goodsmoa_BE.demand.repository.DemandLikeRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DemandLikeService {
    private final DemandLikeRepository demandLikeRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final long CACHE_TTL_MINUTES = 10;

    @Transactional
    public void likePost(Long postId, String userId) {
        redisTemplate.opsForSet().add("user:" + userId + ":liked_posts", postId);
        redisTemplate.opsForSet().add("post:" + postId + ":likes", userId);
        if(Boolean.TRUE.equals(redisTemplate.opsForSet().isMember("pendingUnlikes", userId + ":" + postId))){
            redisTemplate.opsForSet().remove("pendingUnlikes", userId + ":" + postId);
        }
        redisTemplate.opsForSet().add("pendingLikes", userId + ":" + postId);
        redisTemplate.opsForValue().increment("post:" + postId + ":like_count");
    }

    @Transactional
    public void unlikePost(Long postId, String userId) {
        redisTemplate.opsForSet().remove("user:" + userId + ":liked_posts", postId);
        redisTemplate.opsForSet().remove("post:" + postId + ":likes", userId);
        if(Boolean.TRUE.equals(redisTemplate.opsForSet().isMember("pendingLikes", userId + ":" + postId))){
            redisTemplate.opsForSet().remove("pendingLikes", userId + ":" + postId);
        }
        redisTemplate.opsForSet().add("pendingUnlikes", userId + ":" + postId);
        redisTemplate.opsForValue().decrement("post:" + postId + ":like_count");
    }

    // [Read Aside 구현] 좋아요 여부 확인
    public boolean isLiked(Long postId, String userId) {
        String postLikesKey = "post:" + postId + ":likes";

        // 1. Redis 캐시 체크
        Boolean isMember = redisTemplate.opsForSet().isMember(postLikesKey, userId);
        if (Boolean.TRUE.equals(isMember)) return true;

        // 2. 캐시 미스 시 DB 조회
        boolean existsInDB = demandLikeRepository.existsByUserIdAndPostId(userId, postId);

        // 3. DB 결과를 Redis에 캐싱 (TTL 설정)
        if (existsInDB) redisTemplate.opsForSet().add(postLikesKey, userId);

        redisTemplate.expire(postLikesKey, CACHE_TTL_MINUTES, TimeUnit.MINUTES);;

        return existsInDB;
    }

    // [Read Aside 구현] 사용자별 좋아요 목록 조회
    public Set<Long> getLikedPosts(String userId) {
        String userLikedKey = "user:" + userId + ":liked_posts";

        // 1. Redis 캐시 체크
        Set<Object> cachedPosts = redisTemplate.opsForSet().members(userLikedKey);
        if (cachedPosts != null && !cachedPosts.isEmpty()) {
            return cachedPosts.stream()
                    .map(o -> Long.valueOf(o.toString()))
                    .collect(Collectors.toSet());
        }

        // 2. 캐시 미스 시 DB 조회
        Set<Long> dbPosts = demandLikeRepository.findPostIdsByUserId(userId);

        // 3. DB 결과를 Redis에 캐싱 (TTL 설정)
        if (!dbPosts.isEmpty()) {
            redisTemplate.opsForSet().add(userLikedKey, dbPosts.toArray());
        }
        redisTemplate.expire(userLikedKey, CACHE_TTL_MINUTES, TimeUnit.MINUTES);

        return dbPosts;
    }

    // [Read Aside 구현] 게시물별 좋아요 수 조회
    public Long getLikeCount(Long postId) {
        String likeCountKey = "post:" + postId + ":like_count";

        // 1. Redis 캐시 체크
        Object count = redisTemplate.opsForValue().get(likeCountKey);
        if (count != null) return Long.parseLong(count.toString());

        // 2. 캐시 미스 시 DB 조회
        Long dbCount = demandLikeRepository.countByPostId(postId);

        // 3. DB 결과를 Redis에 캐싱 (TTL 설정)
        redisTemplate.opsForValue().set(likeCountKey, dbCount, CACHE_TTL_MINUTES, TimeUnit.MINUTES);

        return dbCount;
    }

    // [Write Back 구현] 현재 위치한 화면 벗어날 때 DB와 동기화
    @Transactional
    public void syncLikesToDB() {
        Set<Object> pendingLikes = redisTemplate.opsForSet().members("pendingLikes");
        Set<Object> pendingUnlikes = redisTemplate.opsForSet().members("pendingUnlikes");

        List<DemandLikeEntity> likesToSave = new ArrayList<>();
        List<DemandLikeEntity> likesToDelete = new ArrayList<>();

        if (pendingLikes != null && !pendingLikes.isEmpty()) {
            pendingLikes.forEach(like -> {
                String[] parts = like.toString().split(":");
                String userId = parts[0];
                Long postId = Long.parseLong(parts[1]);
                String unlikeKey = userId + ":" + postId;

                if(pendingUnlikes != null && !pendingUnlikes.contains(unlikeKey)) {
                    redisTemplate.opsForSet().remove("pendingUnlikes", unlikeKey);
                }else if (demandLikeRepository.existsByUserIdAndPostId(userId, postId)) {
                    likesToSave.add(new DemandLikeEntity(userId, postId));
                }
            });
        }
        if (pendingUnlikes != null && !pendingUnlikes.isEmpty()) {
            pendingUnlikes.forEach(unlike -> {
                String[] parts = unlike.toString().split(":");
                String userId = parts[0];
                Long postId = Long.parseLong(parts[1]);
                String likeKey = userId + ":" + postId;

                if(pendingLikes != null && !pendingLikes.contains(likeKey)) {
                    redisTemplate.opsForSet().remove("pendingLikes", likeKey);
                }else {
                    DemandLikeEntity likeEntity = demandLikeRepository.findByUserIdAndPostId(userId, postId)
                            .orElseThrow(() -> new EntityNotFoundException(postId+"게시물 좋아요 내역 없음"));
                    likesToDelete.add(likeEntity);
                }
            });
        }
        if(!likesToSave.isEmpty()) demandLikeRepository.saveAll(likesToSave);
        if(!likesToDelete.isEmpty()) demandLikeRepository.deleteAll(likesToDelete);

        redisTemplate.delete("pendingLikes");
        redisTemplate.delete("pendingUnlikes");
    }
}
