package com.goodsmoa.goodsmoa_BE.cart.dto.payment;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TossPaymentRequest {

    private String orderCode;       // 주문 ID (내부적으로 관리할 ID)

    private String orderName;     // 주문명 (ex: 상품 이름)

    private String customerName;  // 구매자 이름

    private Integer amount;       // 총 결제 금액

    private String successUrl;    // 결제 성공 시 이동할 URL

    private String failUrl;       // 결제 실패 시 이동할 URL


}
