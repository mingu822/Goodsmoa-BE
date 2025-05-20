package com.goodsmoa.goodsmoa_BE.community.service;

import com.goodsmoa.goodsmoa_BE.category.Entity.Category;
import com.goodsmoa.goodsmoa_BE.category.Repository.CategoryRepository;
import com.goodsmoa.goodsmoa_BE.community.converter.CommunityPostConverter;
import com.goodsmoa.goodsmoa_BE.community.dto.CommunityPostRequest;
import com.goodsmoa.goodsmoa_BE.community.dto.CommunityPostResponse;
import com.goodsmoa.goodsmoa_BE.community.dto.CommunityReplyResponse;
import com.goodsmoa.goodsmoa_BE.community.entity.CommunityLikeEntity;
import com.goodsmoa.goodsmoa_BE.community.entity.CommunityPostEntity;
import com.goodsmoa.goodsmoa_BE.community.repository.CommunityLikeRepository;
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
public class CommunityLikeService {

    private final CommunityPostRepository postRepository;
    private final CommunityLikeRepository likeRepository;




    // 좋아요 토글 기능
    public ResponseEntity<String> toggleLike(UserEntity user, Long postId) {
        Optional<CommunityPostEntity> op = postRepository.findById(postId);
        if (op.isEmpty()) return ResponseEntity.notFound().build();

        CommunityPostEntity post = op.get();

        // 이미 좋아요 눌렀는지 확인
        Optional<CommunityLikeEntity> existingLike = likeRepository.findByPostAndUser(post, user);

        if (existingLike.isPresent()) {
            // 있으면 취소
            likeRepository.delete(existingLike.get());
            return ResponseEntity.ok("좋아요 취소됨");
        } else {
            // 없으면 새로 등록
            CommunityLikeEntity like = CommunityLikeEntity.builder()
                    .post(post)
                    .user(user)
                    .build();
            likeRepository.save(like);
            return ResponseEntity.ok("좋아요 등록됨");
        }
    }

}


