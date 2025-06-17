package com.goodsmoa.goodsmoa_BE.cart.dto.order;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TradeOrderResponse {
    // Order 공통 정보
    private Long orderId;
    private String orderCode;
    private String orderName;
    private Integer productsPrice;
    private Integer deliveryPrice;
    private Integer totalPrice;
    private String mainAddress;
    private String postMemo;
    // TradePost 관련 정보
    private String postTitle;
    private String postThumbnail;
    private String sellerNickname;
}