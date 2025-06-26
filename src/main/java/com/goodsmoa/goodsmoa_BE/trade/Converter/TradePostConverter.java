package com.goodsmoa.goodsmoa_BE.trade.Converter;

import com.goodsmoa.goodsmoa_BE.category.Entity.Category;
import com.goodsmoa.goodsmoa_BE.trade.DTO.Post.*;
import com.goodsmoa.goodsmoa_BE.trade.Entity.TradePostEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor // final 필드에 대한 생성자 주입
public class TradePostConverter {

    private final TradeImageConverter tradeImageConverter;

    // Request -> Entity 변환 (content 필드 반영)
    public TradePostEntity toEntity(TradePostRequest request, Category category, UserEntity user) {
        return TradePostEntity.builder()
                .user(user)
                .category(category)
                .title(request.getTitle())
                .content(request.getContent()) // ✅
                .productPrice(request.getProductPrice())
                .conditionStatus(request.getConditionStatus())
                .tradeStatus(request.getTradeStatus())
                .delivery(request.getDelivery())
                .deliveryPrice(request.getDeliveryPrice())
                .views(0L)
                .createdAt(LocalDateTime.now())
                .direct(request.getDirect())
                .place(request.getPlace())
                .hashtag(request.getHashtag())
                .build();
    }

    // Entity -> Response 변환 (모든 응답 DTO에 content 반영 및 이미지 처리 수정)
    public TradePostResponse toResponse(TradePostEntity entity) {
        return TradePostResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent()) // ✅
                .productPrice(entity.getProductPrice())
                .conditionStatus(entity.getConditionStatus())
                .tradeStatus(entity.getTradeStatus())
                .delivery(entity.getDelivery())
                .direct(entity.getDirect())
                .place(entity.getPlace())
                .thumbnailImage(entity.getThumbnailImage())
                .createdAt(entity.getCreatedAt())
                .hashtag(entity.getHashtag())
                .deliveryPrice(entity.getDeliveryPrice())
                .views(entity.getViews())
                .categoryName(entity.getCategory().getName())
                .user(entity.getUser())
                .productImages(tradeImageConverter.toResponseList(entity.getImage())) // ✅ 수정
                .build();
    }

    public TradePostUpdateResponse upResponse(TradePostEntity entity) {
        return TradePostUpdateResponse.builder()
                .id(entity.getId())
                .content(entity.getContent()) // ✅
                .productPrice(entity.getProductPrice())
                .conditionStatus(entity.getConditionStatus())
                .tradeStatus(entity.getTradeStatus())
                .delivery(entity.getDelivery())
                .direct(entity.getDirect())
                .place(entity.getPlace())
                .thumbnailImage(entity.getThumbnailImage())
                .updatedAt(LocalDateTime.now())
                .hashtag(entity.getHashtag())
                .deliveryPrice(entity.getDeliveryPrice())
                .categoryName(entity.getCategory().getName())
                .userId(entity.getUser().getId())
                .userNickName(entity.getUser().getNickname())
                .productImages(tradeImageConverter.toResponseList(entity.getImage())) // ✅ 수정
                .build();
    }

    public TradePostDetailResponse detailResponse(TradePostEntity entity) {
        return TradePostDetailResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent()) // ✅
                .hashtag(entity.getHashtag())
                .categoryName(entity.getCategory().getName())
                .nickName(entity.getUser().getNickname())
                .userId(entity.getUser().getId())
                .userProfileImage(entity.getUser().getImage())
                .productImages(tradeImageConverter.toResponseList(entity.getImage())) // ✅ 수정
                .thumbnailImage(entity.getThumbnailImage())
                .delivery(entity.getDelivery())
                .deliveryPrice(entity.getDeliveryPrice())
                .productPrice(entity.getProductPrice())
                .direct(entity.getDirect())
                .views(entity.getViews())
                .place(entity.getPlace())
                .build();
    }

    public TradePostPulledResponse pulledResponse(TradePostEntity entity) {
        return TradePostPulledResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent()) // ✅
                .pulledAt(entity.getPulledAt())
                .build();
    }

    // 이 메서드는 content와 관련 없으므로 변경사항 없음
    public TradePostLookResponse lookResponse(TradePostEntity entity) {
        return TradePostLookResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .createdAt(entity.getCreatedAt())
                .productPrice(entity.getProductPrice())
                .categoryName(entity.getCategory().getName())
                .thumbnailImage(entity.getThumbnailImage())
                .views(entity.getViews())
                .hashtag(entity.getHashtag())
                .userId(entity.getUser().getId())
                .userNickName(entity.getUser().getNickname())
                .userImage(entity.getUser().getImage())
                .tradeStatus(entity.getTradeStatus())
                .build();
    }
}