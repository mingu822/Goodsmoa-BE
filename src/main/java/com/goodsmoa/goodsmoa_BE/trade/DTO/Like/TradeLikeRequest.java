package com.goodsmoa.goodsmoa_BE.trade.DTO.Like;

import com.goodsmoa.goodsmoa_BE.trade.Entity.TradeLikeEntity;
import com.goodsmoa.goodsmoa_BE.trade.Entity.TradePostEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class TradeLikeRequest {

    @NotNull
    private Long trade;


    public TradeLikeEntity toEntity(TradePostEntity trade, UserEntity user) {
        return TradeLikeEntity.builder()
                .trade(trade)
                .user(user)
                .build();
    }
}


