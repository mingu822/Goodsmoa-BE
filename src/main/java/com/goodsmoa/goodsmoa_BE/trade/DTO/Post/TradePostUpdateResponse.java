package com.goodsmoa.goodsmoa_BE.trade.DTO.Post;

import com.goodsmoa.goodsmoa_BE.trade.Entity.TradeImageEntity;
import com.goodsmoa.goodsmoa_BE.trade.Entity.TradePostEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradePostUpdateResponse {

    private Long id;

    private String content;

    private Integer productPrice;

    private TradePostEntity.ConditionStatus conditionStatus;

    private TradePostEntity.TradeStatus tradeStatus;

    private Boolean delivery;

    private long deliveryPrice;

    private String thumbnailImage;

    private Boolean direct;

    private String place;

    private String hashtag;

    private String categoryName;

    private String userId;

    private List<TradeImageEntity> tradeImage;
}
