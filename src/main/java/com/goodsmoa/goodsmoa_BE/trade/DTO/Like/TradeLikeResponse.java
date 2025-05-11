package com.goodsmoa.goodsmoa_BE.trade.DTO.Like;


import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TradeLikeResponse {
    private Long tradeId;

    private String userId;

    private String nickName;

    private String userImage;

    private LocalDateTime createdAt;

    private String title;

    private String thumbnailImage;

    private Integer productPrice;
}
