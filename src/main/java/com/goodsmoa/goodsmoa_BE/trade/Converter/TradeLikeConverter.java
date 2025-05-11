package com.goodsmoa.goodsmoa_BE.trade.Converter;

import com.goodsmoa.goodsmoa_BE.trade.DTO.Like.TradeLikeResponse;
import com.goodsmoa.goodsmoa_BE.trade.Entity.TradeLikeEntity;
import com.goodsmoa.goodsmoa_BE.trade.Entity.TradePostEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class TradeLikeConverter {
    public TradeLikeEntity toEntity(TradePostEntity trade, UserEntity user) {
        return TradeLikeEntity.builder()
                .trade(trade)
                .user(user)
                .build();
    }

    public TradeLikeResponse toResponse(TradeLikeEntity entity) {

        return TradeLikeResponse.builder()
                .tradeId(entity.getTrade().getId())
                .title(entity.getTrade().getTitle())
                .userId(entity.getUser().getId())
                .createdAt(entity.getTrade().getCreatedAt())
                .userImage(entity.getUser().getImage())
                .nickName(entity.getUser().getNickname())
                .thumbnailImage(entity.getTrade().getThumbnailImage()) // 필요 시 엔티티에 getter 추가
                .productPrice(entity.getTrade().getProductPrice())
                .build();
    }
    //프론트 -> 이미지 저장 x ->
}
