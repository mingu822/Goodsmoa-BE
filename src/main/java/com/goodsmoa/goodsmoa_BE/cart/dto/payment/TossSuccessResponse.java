package com.goodsmoa.goodsmoa_BE.cart.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TossSuccessResponse {

    private String paymentKey;      // 토스에서 제공하는 결제 키

    private String orderId;         // 주문 ID

    private Integer amount;             // 결제 금액

    private String orderName;       // 상품 이름 (ex. "LG TWINS 포토카드")

    private String customerName;    // 구매자 이름

    private String method;          // 결제 방식 (e.g. 카드, 가상계좌 등)

}
