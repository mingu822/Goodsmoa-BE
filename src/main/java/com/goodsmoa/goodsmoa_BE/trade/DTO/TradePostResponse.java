package com.goodsmoa.goodsmoa_BE.trade.DTO;

import com.goodsmoa.goodsmoa_BE.category.Entity.Category;
import com.goodsmoa.goodsmoa_BE.trade.Entity.TradePostEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TradePostResponse {
    private String title;

    private String content;

    private Integer productPrice;

    private TradePostEntity.ConditionStatus conditionStatus;

    private TradePostEntity.TradeStatus tradeStatus;

    private Boolean deliveryPrice;

    private Boolean direct;

    private String place;

    private String hashtag;

    private LocalDateTime createdAt;

    private int categoryId;

    private String userId;
}
