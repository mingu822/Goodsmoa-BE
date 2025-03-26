package com.goodsmoa.goodsmoa_BE.trade.Converter;

import com.goodsmoa.goodsmoa_BE.category.Entity.Category;
import com.goodsmoa.goodsmoa_BE.trade.DTO.Post.*;
import com.goodsmoa.goodsmoa_BE.trade.Entity.TradePostEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TradePostConverter {

    // Entity → Response 변환
    public TradePostResponse toResponse(TradePostEntity entity) {
        return TradePostResponse.builder()
                .user(entity.getUser())
                .title(entity.getTitle())
                .content(entity.getContent())
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
                .categoryName(entity.getCategory().getName()) // 수정: getCategory().getId()
                .tradeImage(entity.getImage())
                .build();
    }

    public TradePostUpdateResponse upResponse(TradePostEntity entity){
        return TradePostUpdateResponse.builder()
                .userId(entity.getUser().getId())
                .categoryName(entity.getCategory().getName())
                .conditionStatus(entity.getConditionStatus())
                .tradeStatus(entity.getTradeStatus())
                .delivery(entity.getDelivery())
                .content(entity.getContent())
                .deliveryPrice(entity.getDeliveryPrice())
                .direct(entity.getDirect())
                .hashtag(entity.getHashtag())
                .tradeImage(entity.getImage())
                .productPrice(entity.getProductPrice())
                .thumbnailImage(entity.getThumbnailImage())
                .id(entity.getId())
                .place(entity.getPlace())
                .build();

    }

    // Request → Entity 변환
    public TradePostEntity toEntity(TradePostRequest request, Category category, UserEntity user) {
        return TradePostEntity.builder()
                .user(user)  // 수정: User 객체를 직접 전달
                .category(category)  // 수정: Category 객체를 직접 전달
                .title(request.getTitle())
                .content(request.getContent())
                .productPrice(request.getProductPrice())
                .conditionStatus(request.getConditionStatus())
                .tradeStatus(request.getTradeStatus())
                .delivery(request.getDelivery())
                .thumbnailImage(request.getThumbnailImage())
                .deliveryPrice(request.getDeliveryPrice())
                .views(0L)
                .createdAt(LocalDateTime.now())
                .direct(request.getDirect())
                .place(request.getPlace())
                .hashtag(request.getHashtag())
                .build();
    }

    public TradePostDetailResponse detailResponse(TradePostEntity entity) {
        return TradePostDetailResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .hashtag(entity.getHashtag())
                .categoryName(entity.getCategory().getName())
                .userName(entity.getUser().getName())
                .imageUrl(entity.getImage())
                .delivery(entity.getDelivery())
                .deliveryPrice(entity.getDeliveryPrice())
                .deliveryPrice(entity.getProductPrice())
                .direct(entity.getDirect())
                .views(entity.getViews())
                .place(entity.getPlace())
                .build();
    }

    public TradePostPulledResponse pulledResponse(TradePostEntity entity) {
        return TradePostPulledResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .pulledAt(entity.getPulledAt())
                .build();
    }
}

