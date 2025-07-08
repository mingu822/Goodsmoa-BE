package com.goodsmoa.goodsmoa_BE.commission.service;

import com.goodsmoa.goodsmoa_BE.commission.converter.CommissionLikeConverter;
import com.goodsmoa.goodsmoa_BE.commission.dto.like.CommissionLikeResponse;
import com.goodsmoa.goodsmoa_BE.commission.entity.CommissionLikeEntity;
import com.goodsmoa.goodsmoa_BE.commission.entity.CommissionPostEntity;
import com.goodsmoa.goodsmoa_BE.commission.repository.CommissionLikeRepository;
import com.goodsmoa.goodsmoa_BE.commission.repository.CommissionRepository;
import com.goodsmoa.goodsmoa_BE.product.dto.like.ProductLikeResponse;
import com.goodsmoa.goodsmoa_BE.product.entity.ProductLikeEntity;
import com.goodsmoa.goodsmoa_BE.product.entity.ProductPostEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommissionLikeService {

    private final CommissionRepository commissionRepository;
    private final CommissionLikeRepository commissionLikeRepository;
    private final CommissionLikeConverter commissionLikeConverter;
    private final CommissionRedisService commissionRedisService;

    @Transactional
    public ResponseEntity<Void> like(UserEntity user, Long id) {

        CommissionPostEntity entity = commissionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 커미션이 존재하지 않습니다."));

        if(commissionLikeRepository.existsByCommissionIdAndUserId(entity,user)){
            throw new IllegalArgumentException("이미 찜한 상품입니다.");
        }

        CommissionLikeEntity like = commissionLikeConverter.toEntity(entity,user);
        commissionLikeRepository.save(like);

        commissionRedisService.increaseLikeCount(id);

        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<Void> unLike(UserEntity user, Long id) {
        CommissionPostEntity entity = commissionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 커미션이 존재하지 않습니다."));

        CommissionLikeEntity like = commissionLikeRepository.findByCommissionIdAndUserId(entity,user);

        commissionLikeRepository.delete(like);

        commissionRedisService.decreaseLikeCount(id);

        return ResponseEntity.ok().build();
    }

    // 내가 좋아요 한 글 가져오기
    public ResponseEntity<Page<CommissionLikeResponse>> getLikes(UserEntity user, Pageable pageable) {
        Page<CommissionLikeEntity> likePage = commissionLikeRepository.findByUserId(user, pageable);

        Page<CommissionLikeResponse> responses = likePage.map(commissionLikeConverter::toResponse);

        return ResponseEntity.ok(responses);
    }

    public ResponseEntity<CommissionLikeResponse> getSingleLiked(UserEntity user, Long id) {
        CommissionPostEntity entity = commissionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 커미션이 존재하지 않습니다."));

        CommissionLikeEntity like = commissionLikeRepository.findByCommissionIdAndUserId(entity,user);

        CommissionLikeResponse res = commissionLikeConverter.toResponse(like);

        return ResponseEntity.ok(res);
    }
}
