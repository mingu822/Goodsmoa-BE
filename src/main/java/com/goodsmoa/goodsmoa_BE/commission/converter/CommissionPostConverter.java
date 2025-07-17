package com.goodsmoa.goodsmoa_BE.commission.converter;

import com.goodsmoa.goodsmoa_BE.category.Entity.Category;
import com.goodsmoa.goodsmoa_BE.commission.dto.apply.ReceivedListResponse;
import com.goodsmoa.goodsmoa_BE.commission.dto.apply.ResponseContentDto;
import com.goodsmoa.goodsmoa_BE.commission.dto.apply.SubscriptionListResponse;
import com.goodsmoa.goodsmoa_BE.commission.dto.apply.SubscriptionResponse;
import com.goodsmoa.goodsmoa_BE.commission.dto.detail.CommissionDetailRequest;
import com.goodsmoa.goodsmoa_BE.commission.dto.detail.CommissionDetailResponse;
import com.goodsmoa.goodsmoa_BE.commission.dto.post.*;
import com.goodsmoa.goodsmoa_BE.commission.entity.CommissionDetailEntity;
import com.goodsmoa.goodsmoa_BE.commission.entity.CommissionDetailResponseEntity;
import com.goodsmoa.goodsmoa_BE.commission.entity.CommissionPostEntity;
import com.goodsmoa.goodsmoa_BE.commission.entity.CommissionSubscriptionEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Component
public class CommissionPostConverter {

    private final CommissionDetailConverter commissionDetailConverter;

    // 커미션 생성을 위한 변환
    public CommissionPostEntity saveToEntity(PostRequest request, UserEntity user, Category category) {
        return CommissionPostEntity.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .thumbnailImage(request.getThumbnailImage())
                .requestLimited(request.getRequestLimited())
                .minimumPrice(request.getMinimumPrice())
                .maximumPrice(request.getMaximumPrice())
                .createdAt(LocalDateTime.now())
                .hashtag(request.getHashtag())
                .category(category)
                .status(true)
                .views(0L)
                .likes(0L)
                .user(user)
                .build();
    }

    public PostResponse toResponse(CommissionPostEntity entity) {
        return PostResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .thumbnailImage(entity.getThumbnailImage())
                .userName(entity.getUser().getNickname())
                .userImage(entity.getUser().getImage())
                .views(entity.getViews())
                .hashtag(entity.getHashtag())
                .build();
    }

    public PostDetailResponse detailPostToResponse(CommissionPostEntity increaseEntity, List<CommissionDetailEntity> detailEntities) {
        return PostDetailResponse.builder()
                .id(increaseEntity.getId())
                .title(increaseEntity.getTitle())
                .content(increaseEntity.getContent())
                .userId(increaseEntity.getUser().getId())
                .categoryName(increaseEntity.getCategory().getName())
                .thumbnailImage(increaseEntity.getThumbnailImage())
                .requestLimited(increaseEntity.getRequestLimited())
                .minimumPrice(increaseEntity.getMinimumPrice())
                .maximumPrice(increaseEntity.getMaximumPrice())
                .hashtag(increaseEntity.getHashtag())
                .views(increaseEntity.getViews())
                .commissionDetail(detailEntities.stream().map(commissionDetailConverter::detailToResponse).toList())
                .build();
    }

    // 신청을 위한 변환
    public CommissionSubscriptionEntity saveToSubscriptionEntity(UserEntity user, CommissionPostEntity postEntity) {
        return CommissionSubscriptionEntity.builder()
                .commissionId(postEntity)
                .userId(user)
                .requestStatus(CommissionSubscriptionEntity.RequestStatus.확인중)
                .createdAt(LocalDateTime.now())
                .build();
    }

    // 신청완료 후 response 값
    public SubscriptionResponse subscriptionResponse(CommissionPostEntity postEntity, List<CommissionDetailEntity> detailEntities, List<CommissionDetailResponseEntity> responses, CommissionSubscriptionEntity subscriptionEntity) {
        List<ResponseContentDto> resContentList = responses.stream()
                .map(res -> ResponseContentDto.builder()
                        .id(res.getId())
                        .content(res.getResContent())
                        .build())
                .toList();
        return SubscriptionResponse.builder()
                .title(postEntity.getTitle())
                .postId(postEntity.getId())
                .categoryName(postEntity.getCategory().getName())
                .thumbnailImage(postEntity.getThumbnailImage())
                .minimumPrice(postEntity.getMinimumPrice())
                .maximumPrice(postEntity.getMaximumPrice())
                .commissionDetail(detailEntities.stream().map(commissionDetailConverter::detailToResponse).toList())
                .resContentList(resContentList)
                .requestStatus(subscriptionEntity.getRequestStatus().toString())
                .clientId(subscriptionEntity.getUserId().getId())
                .creatorId(subscriptionEntity.getCommissionId().getUser().getId())
                .build();
    }

    public SubscriptionListResponse toSubscriptionListResponse(CommissionSubscriptionEntity entity) {
        return SubscriptionListResponse.builder()
                .id(entity.getId())
                .commissionId(entity.getCommissionId().getId())
                .title(entity.getCommissionId().getTitle())
                .requestStatus(entity.getRequestStatus().name())
                .categoryName(entity.getCommissionId().getCategory().getName())
                .sellerName(entity.getCommissionId().getUser().getNickname())
                .sellerId(entity.getCommissionId().getUser().getId())
                .thumbnailImage(entity.getCommissionId().getThumbnailImage())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public ReceivedListResponse toReceivedListResponse(CommissionSubscriptionEntity entity) {
        return ReceivedListResponse.builder()
                .id(entity.getId())
                .commissionId(entity.getCommissionId().getId())
                .title(entity.getCommissionId().getTitle())
                .requestStatus(entity.getRequestStatus().name())
                .categoryName(entity.getCommissionId().getCategory().getName())
                .applicantName(entity.getUserId().getNickname())
                .applicantId(entity.getUserId().getId())
                .thumbnailImage(entity.getCommissionId().getThumbnailImage())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
