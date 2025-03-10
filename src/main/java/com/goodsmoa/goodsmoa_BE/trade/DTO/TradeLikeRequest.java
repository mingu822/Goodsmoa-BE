package com.goodsmoa.goodsmoa_BE.trade.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TradeLikeRequest {

    @NotNull
    private Long tradeId;

    @NotNull
    private Long userId;

    public TradeLikeRequest(Long tradeId, Long userId) {
        this.tradeId = tradeId;
        this.userId = userId;
    }

    }