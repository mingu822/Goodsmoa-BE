package com.goodsmoa.goodsmoa_BE.demand.service;

import com.goodsmoa.goodsmoa_BE.demand.converter.DemandPostConverter;
import com.goodsmoa.goodsmoa_BE.demand.dto.post.DemandPostResponse;
import com.goodsmoa.goodsmoa_BE.demand.dto.post.DemandSearchRequest;
import com.goodsmoa.goodsmoa_BE.demand.entity.DemandLikeEntity;
import com.goodsmoa.goodsmoa_BE.demand.entity.DemandPostEntity;
import com.goodsmoa.goodsmoa_BE.demand.repository.DemandLikeRepository;
import com.goodsmoa.goodsmoa_BE.demand.repository.DemandPostRepository;
import com.goodsmoa.goodsmoa_BE.search.dto.SearchDocWithUserResponse;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DemandLikeService {
    private final DemandLikeRepository demandLikeRepository;
    private final DemandPostRepository demandPostRepository;
    private final DemandPostConverter demandPostConverter;
    private final DemandRedisService demandRedisService;

    @Transactional
    public String toggleLike(UserEntity user, Long postId) {
        DemandPostEntity post = demandPostRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 게시물입니다"));

        if (isLike(user, postId)) {
            // 좋아요 취소 로직
            DemandLikeEntity likeEntity = demandLikeRepository.findByUserIdAndPostId(user.getId(), postId)
                    .orElseThrow(() -> new EntityNotFoundException("좋아요 기록이 존재하지 않습니다"));

            demandLikeRepository.delete(likeEntity);
            demandRedisService.decreaseLikeCount(postId);
            return "좋아요 취소 완료";
        } else {
            // 좋아요 추가 로직
            if (demandLikeRepository.existsByUserIdAndPostId(user.getId(), postId)) {
                throw new IllegalStateException("이미 좋아요 한 게시물입니다");
            }

            demandLikeRepository.save(new DemandLikeEntity(user.getId(), postId));
            demandRedisService.increaseLikeCount(postId);
            return "좋아요 완료";
        }
    }


    @Transactional
    public void likePost(UserEntity user, Long postId) {
        if(isLike(user, postId)) throw new EntityNotFoundException("이미 좋아요 했습니다.");
        demandLikeRepository.save(new DemandLikeEntity(user.getId(), postId));
        demandRedisService.increaseLikeCount(postId);
    }

    @Transactional
    public void unlikePost(UserEntity user, Long postId) {
        DemandLikeEntity demandLikeEntity = findByIdWithThrow(user.getId(), postId);
        validateUserAuthorization(user, demandLikeEntity);
        demandLikeRepository.delete(demandLikeEntity);
        demandRedisService.decreaseLikeCount(postId);
    }

    // 좋아요 여부 확인
    public Boolean isLike(UserEntity user, Long postId) {
        return demandLikeRepository.existsByUserIdAndPostId(user.getId(), postId);
    }

    // 글의 좋아요 갯수
    public Long countLike(Long postId){
        return demandLikeRepository.countByPostId(postId);
    }
    
    // 유저가 좋아요 한 글 목록
    public Page<DemandPostResponse> getDemandPostListByUser(UserEntity user, DemandSearchRequest request){
        Page<DemandLikeEntity> pageResult;
        PageRequest pageRequest = PageRequest.of(
                request.getPage(),
                request.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        pageResult = demandLikeRepository.findByUserId(user.getId(), pageRequest);
        List<Long> postIds = pageResult.getContent().stream()
                .map(DemandLikeEntity::getPostId)
                .toList();
        List<DemandPostEntity> posts = demandPostRepository.findAllById(postIds);
        Map<Long, DemandPostEntity> postMap = posts.stream()
                .collect(Collectors.toMap(DemandPostEntity::getId, Function.identity()));

        List<DemandPostResponse> responses = new ArrayList<>();
        for (DemandLikeEntity like : pageResult.getContent()) {
            DemandPostEntity post = postMap.get(like.getPostId());
            if (post != null) {
                responses.add(demandPostConverter.toResponse(post));
            }
        }
        return new PageImpl<>(responses, pageRequest, pageResult.getTotalElements());
    }

    private DemandLikeEntity findByIdWithThrow(String userId, Long postId) {
        return demandLikeRepository.findByUserIdAndPostId(userId, postId)
                .orElseThrow(() -> new EntityNotFoundException("해당 좋아요가 존재하지 않습니다."));
    }

    // 권한 조회
    private void validateUserAuthorization(UserEntity user, DemandLikeEntity entity) {
        if (!entity.getUserId().equals(user.getId())) {
            throw new AccessDeniedException("권한이 없습니다");
        }
    }

    // 수요조사 상세보기 시 좋아요 여부 추가
    public boolean addLikeStatus(UserEntity user, Long postId) {
        return demandLikeRepository.existsByUserIdAndPostId(user.getId(), postId);
    }

    // 검색결과에 좋아요 여부 추가
    public void addLikeStatus(UserEntity user, List<SearchDocWithUserResponse> responses) {
        // 1. 응답에서 숫자 ID 추출 (예: "DEMAND_16" → 16)
        List<Long> numericPostIds = new ArrayList<>();
        Map<Long, SearchDocWithUserResponse> idToResponseMap = new HashMap<>();

        for (SearchDocWithUserResponse res : responses) {
            try {
                // ID 형식: "BOARD_ID"
                String[] parts = res.getId().split("_");
                if (parts.length >= 2) {
                    Long numericId = Long.parseLong(parts[1]);
                    numericPostIds.add(numericId);
                    idToResponseMap.put(numericId, res);
                }
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                log.warn("Invalid ID format: {}", res.getId());
            }
        }

        // 2. 숫자 ID로 좋아요 여부 일괄 조회
        if (!numericPostIds.isEmpty()) {
            Set<Long> likedNumericIds = demandLikeRepository.findLikedIdsByUserAndPosts(
                    user.getId(), numericPostIds
            );

            // 3. 응답 객체에 좋아요 여부 매핑
            idToResponseMap.forEach((numericId, response) -> {
                response.setLiked(likedNumericIds.contains(numericId));
            });
        }
    }
}
//    private final RedisTemplate<String, Object> redisTemplate;
//
//    private static final long CACHE_TTL_MINUTES = 10;
//
//    private static final String STATE_KEY_PREFIX = "like_state:"; // 🔥 Key 포맷: "like_state:{userId}:{postId}"
//
//    public void likePost(Long postId, String userId) {
//        String stateKey = STATE_KEY_PREFIX + userId + ":" + postId;
//        // 1. 최신 상태 저장 (1=좋아요)
//        redisTemplate.opsForValue().set(stateKey, "1", 1, TimeUnit.HOURS);
//
//        // 2. 실시간 데이터 업데이트
//        redisTemplate.opsForSet().add("user:" + userId + ":liked_posts", postId);
//        redisTemplate.opsForSet().add("post:" + postId + ":likes", userId);
//        redisTemplate.opsForValue().increment("post:" + postId + ":like_count");
//    }
//
//    // 좋아요 취소 시
//    public void unlikePost(Long postId, String userId) {
//        String stateKey = STATE_KEY_PREFIX + userId + ":" + postId;
//        // 1. 최신 상태 저장 (0=취소)
//        redisTemplate.opsForValue().set(stateKey, "0", 1, TimeUnit.HOURS);
//
//        // 2. 실시간 데이터 업데이트
//        redisTemplate.opsForSet().remove("user:" + userId + ":liked_posts", postId);
//        redisTemplate.opsForSet().remove("post:" + postId + ":likes", userId);
//        redisTemplate.opsForValue().decrement("post:" + postId + ":like_count");
//    }
//
//    // [Read Aside 구현] 좋아요 여부 확인
//    public boolean isLiked(Long postId, String userId) {
//        String postLikesKey = "post:" + postId + ":likes";
//
//        // 1. Redis 캐시 체크
//        Boolean isMember = redisTemplate.opsForSet().isMember(postLikesKey, userId);
//        if (Boolean.TRUE.equals(isMember)) return true;
//
//        // 2. 캐시 미스 시 DB 조회
//        boolean existsInDB = demandLikeRepository.existsByUserIdAndPostId(userId, postId);
//
//        // 3. DB 결과를 Redis 에 캐싱 (TTL 설정)
//        if (existsInDB) redisTemplate.opsForSet().add(postLikesKey, userId);
//
//        redisTemplate.expire(postLikesKey, CACHE_TTL_MINUTES, TimeUnit.MINUTES);;
//
//        return existsInDB;
//    }
//
//    // [Read Aside 구현] 사용자별 좋아요 목록 조회
//    public Set<Long> getLikedPosts(String userId) {
//        String userLikedKey = "user:" + userId + ":liked_posts";
//
//        // 1. Redis 캐시 체크
//        Set<Object> cachedPosts = redisTemplate.opsForSet().members(userLikedKey);
//        if (cachedPosts != null && !cachedPosts.isEmpty()) {
//            return cachedPosts.stream()
//                    .map(o -> Long.valueOf(o.toString()))
//                    .collect(Collectors.toSet());
//        }
//
//        // 2. 캐시 미스 시 DB 조회
//        Set<Long> dbPosts = demandLikeRepository.findPostIdsByUserId(userId);
//
//        // 3. DB 결과를 Redis 에 캐싱 (TTL 설정)
//        if (!dbPosts.isEmpty()) redisTemplate.opsForSet().add(userLikedKey, dbPosts.toArray());
//
//        redisTemplate.expire(userLikedKey, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
//
//        return dbPosts;
//    }
//
//    // [Read Aside 구현] 게시물별 좋아요 수 조회
//    public Long getLikeCount(Long postId) {
//        String likeCountKey = "post:" + postId + ":like_count";
//
//        // 1. Redis 캐시 체크
//        Object count = redisTemplate.opsForValue().get(likeCountKey);
//        if (count != null) return Long.parseLong(count.toString());
//
//        // 2. 캐시 미스 시 DB 조회
//        Long dbCount = demandLikeRepository.countByPostId(postId);
//
//        // 3. DB 결과를 Redis에 캐싱 (TTL 설정)
//        redisTemplate.opsForValue().set(likeCountKey, dbCount, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
//
//        return dbCount;
//    }
//
//    // [Write Back 구현] 현재 위치한 화면 벗어날 때 DB와 동기화
//    // 동 기화 메서드
//    @Transactional
//    public void syncLikesToDB() {
//        // 1. 모든 상태 키 스캔 (패턴: "like_state:*")
//        Set<String> stateKeys = scanKeys(STATE_KEY_PREFIX + "*");
//
//        List<DemandLikeEntity> toSave = new ArrayList<>();
//        List<DemandLikeEntity> toDelete = new ArrayList<>();
//
//        for (String key : stateKeys) {
//            // 2. 키에서 userId, postId 추출
//            String[] parts = key.replace(STATE_KEY_PREFIX, "").split(":");
//            String userId = parts[0];
//            Long postId = Long.parseLong(parts[1]);
//
//            // 3. 최신 상태 값 조회
//            String state = (String) redisTemplate.opsForValue().get(key);
//
//            // 4. 상태에 따라 처리
//            if ("1".equals(state)) {
//                toSave.add(new DemandLikeEntity(userId, postId));
//            } else if ("0".equals(state)) {
//                toDelete.add(new DemandLikeEntity(userId, postId));
//            }
//
//            // 5. 처리된 키 삭제
//            redisTemplate.delete(key);
//        }
//
//        // 6. DB 반영
//        if (!toSave.isEmpty()) {
//            demandLikeRepository.saveAll(toSave); // 중복은 DB UNIQUE 제약으로 자동 필터링
//        }
//        if (!toDelete.isEmpty()) {
//            demandLikeRepository.deleteAll(toDelete);
//        }
//    }
//
//    private Set<String> scanKeys(String pattern){
//        return redisTemplate.execute((RedisCallback<Set<String>>) connection ->{
//            Set<String> keys = new HashSet<>();
//            ScanOptions options = ScanOptions.scanOptions().match(pattern).count(100).build();
//            try( var cursor = connection.scan(options)){
//                while(cursor.hasNext()){
//                    byte[] keyBytes = cursor.next();
//                    keys.add(new String(keyBytes , StandardCharsets.UTF_8));
//                }
//            }catch (Exception e){
//                log.error("Redis scan error: ",e);
//            }
//            return keys;
//        } );
//    }
//}