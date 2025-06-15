package com.goodsmoa.goodsmoa_BE.cart.dto.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public  class TradeOrderRequest {

    // 어떤 중고거래 게시글인지 식별
    private Long tradePostId;

    // 수령인 정보 (이 부분은 공통)
    private String recipientName;
    private String phoneNumber;
    private String zipCode;
    private String mainAddress;
    private String detailedAddress;
    private String postMemo;
}
