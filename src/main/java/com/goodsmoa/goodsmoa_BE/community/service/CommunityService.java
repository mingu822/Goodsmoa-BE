package com.goodsmoa.goodsmoa_BE.community.service;

import com.goodsmoa.goodsmoa_BE.category.Entity.Category;

import com.goodsmoa.goodsmoa_BE.category.Repository.CategoryRepository;
import com.goodsmoa.goodsmoa_BE.community.converter.CommunityPostConverter;

import com.goodsmoa.goodsmoa_BE.community.dto.CommunityPostRequest;
import com.goodsmoa.goodsmoa_BE.community.dto.CommunityPostResponse;
import com.goodsmoa.goodsmoa_BE.community.dto.CommunityReplyResponse;
import com.goodsmoa.goodsmoa_BE.community.entity.CommunityPostEntity;
import com.goodsmoa.goodsmoa_BE.community.repository.CommunityPostRepository;
import com.goodsmoa.goodsmoa_BE.community.repository.CommunityReplyRepository;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;


@Slf4j
@Service
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityPostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final CommunityPostConverter converter;
    private final StringRedisTemplate redisTemplate;
    private final CommunityReplyRepository replyRepository;
    private final CommunityReplyService replyService;


    // 커뮤니티 글 생성
    public ResponseEntity<CommunityPostResponse> createPost(UserEntity user, CommunityPostRequest req) {
        Optional<Category> oc = categoryRepository.findByName(req.getCategoryName());
        // 카테고리 못찾으면 404 응답을 줘서 없다고 알림
        if (oc.isEmpty()) return ResponseEntity.badRequest().build();
        // DTO → Entity 변환
        CommunityPostEntity entity = converter.toEntity(user, oc.get(), req);
        // DB 저장
        CommunityPostEntity saved = postRepository.save(entity);

        // 댓글은 아직 없으니 0으로 고정
        Long replyCount = 0L;
        List<CommunityReplyResponse> replies = List.of(); // 아직 댓글 없으니 빈 리스트

        return ResponseEntity.ok(converter.toResponseDto(saved, replyCount, replies));
    }


    //커뮤니티 글 수정( !!  본인만 수정 가능)
    public ResponseEntity<CommunityPostResponse> updatePost(UserEntity user, Long id, CommunityPostRequest req) {
        Optional<CommunityPostEntity> oe = postRepository.findById(id);
        if (oe.isEmpty()) return ResponseEntity.notFound().build();

        CommunityPostEntity entity = oe.get();

        // 본인이 아니면 권한없음
        if (!entity.getUser().getId().equals(user.getId()))
            return ResponseEntity.status(403).build();

        Optional<Category> oc = categoryRepository.findByName(req.getCategoryName());
        if (oc.isEmpty()) return ResponseEntity.badRequest().build();

        // 수정 메서드로 업데이트
        entity.updatePost(req.getTitle(), req.getContent(), req.getDetailCategory(), oc.get());

        // 저장 후 응답
        CommunityPostEntity updated = postRepository.save(entity);

        // 댓글 수 조회 후 포함
        Long replyCount = replyRepository.countByPostId(updated.getId());

        List<CommunityReplyResponse> replies = replyService.getReplies(updated.getId());

        return ResponseEntity.ok(converter.toResponseDto(updated, replyCount, replies));
    }


    //커뮤니티 글 조회
     //- 조회수 redis->Db비동기 저장

    public ResponseEntity<CommunityPostResponse> getPost(Long id) {
        Optional<CommunityPostEntity> oe = postRepository.findById(id);
        if (oe.isEmpty()) return ResponseEntity.notFound().build();

        CommunityPostEntity entity = oe.get();

        // Redis 조회수 반영
        String redisKey = "communityPost:view:" + id;
        Long newViewCount = redisTemplate.opsForValue().increment(redisKey);
        redisTemplate.expire(redisKey, 1, TimeUnit.DAYS);
        log.info("📌 조회수 redis 반영: 게시글 [{}]의 조회수가 Redis에서 {}로 증가함", id, newViewCount);

        // 댓글 수 조회
        Long replyCount = replyRepository.countByPostId(id);

        List<CommunityReplyResponse> replies = replyService.getReplies(id); // ← 댓글 트리 가져오기

        return ResponseEntity.ok(converter.toResponseDto(entity, replyCount, replies));
    }


    // 커뮤니티 글 삭제
    // - 작성자 본인 또는 관리자만 삭제 가능
    public ResponseEntity<String> deletePost(UserEntity user, Long id) {
        Optional<CommunityPostEntity> oe = postRepository.findById(id);
        if (oe.isEmpty()) return ResponseEntity.notFound().build();

        CommunityPostEntity entity = oe.get();

        // 작성자 본인이 아니고 + 관리자도 아니라면 권한 없음
        if (!entity.getUser().getId().equals(user.getId()) && !user.getRole().equals("ROLE_ADMIN")) {
            return ResponseEntity.status(403).body("삭제 권한이 없습니다.");
        }

        postRepository.delete(entity);
        return ResponseEntity.ok("삭제가 완료되었습니다.");
    }


}


