package com.goodsmoa.goodsmoa_BE.trade.Converter;

import com.goodsmoa.goodsmoa_BE.category.Entity.Category;
import com.goodsmoa.goodsmoa_BE.trade.DTO.TradePostRequest;
import com.goodsmoa.goodsmoa_BE.trade.DTO.TradePostResponse;
import com.goodsmoa.goodsmoa_BE.trade.Entity.TradePostEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class TradePostConverter {

    // Entity → Response 변환
    public TradePostResponse toResponse(TradePostEntity entity) {
        return TradePostResponse.builder()
                .userId(entity.getUser().getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .productPrice(entity.getProductPrice())
                .conditionStatus(entity.getConditionStatus())
                .tradeStatus(entity.getTradeStatus())
                .deliveryPrice(entity.getDeliveryPrice())
                .direct(entity.getDirect())
                .place(entity.getPlace())
                .hashtag(entity.getHashtag())
                .categoryId(entity.getCategory().getId()) // 수정: getCategory().getId()
                .build();
    }

    // Request → Entity 변환
    public TradePostEntity toEntity(TradePostRequest request, UserEntity user, Category category) {
        return TradePostEntity.builder()
                .user(user)  // 수정: User 객체를 직접 전달
                .category(category)  // 수정: Category 객체를 직접 전달
                .title(request.getTitle())
                .content(request.getContent())
                .productPrice(request.getProductPrice())
                .conditionStatus(request.getConditionStatus())
                .tradeStatus(request.getTradeStatus())
                .deliveryPrice(request.getDeliveryPrice())
                .direct(request.getDirect())
                .place(request.getPlace())
                .hashtag(request.getHashtag())
                .build();
    }
}
