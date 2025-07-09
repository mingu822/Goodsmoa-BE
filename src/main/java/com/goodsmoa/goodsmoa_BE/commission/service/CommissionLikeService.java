package com.goodsmoa.goodsmoa_BE.commission.service;

import com.goodsmoa.goodsmoa_BE.commission.converter.CommissionLikeConverter;
import com.goodsmoa.goodsmoa_BE.commission.dto.like.CommissionLikeResponse;
import com.goodsmoa.goodsmoa_BE.commission.entity.CommissionLikeEntity;
import com.goodsmoa.goodsmoa_BE.commission.entity.CommissionPostEntity;
import com.goodsmoa.goodsmoa_BE.commission.repository.CommissionLikeRepository;
import com.goodsmoa.goodsmoa_BE.commission.repository.CommissionRepository;
import com.goodsmoa.goodsmoa_BE.search.dto.SearchDocWithUserResponse;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
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

    public void addLikeStatus(UserEntity user, List<SearchDocWithUserResponse> content) {
        // 1. 응답에서 숫자 ID 추출 (예: "DEMAND_16" → 16)
        List<Long> numericPostIds = new ArrayList<>();
        Map<Long, SearchDocWithUserResponse> idToResponseMap = new HashMap<>();

        for (SearchDocWithUserResponse res : content) {
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
            Set<Long> likedNumericIds = commissionLikeRepository.findLikedIdsByUserIdAndCommissionId(
                    user.getId(), numericPostIds
            );

            // 3. 응답 객체에 좋아요 여부 매핑
            idToResponseMap.forEach((numericId, response) -> {
                response.setLiked(likedNumericIds.contains(numericId));
            });
        }
    }
}
