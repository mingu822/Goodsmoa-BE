package com.goodsmoa.goodsmoa_BE.cart.dto.delivery;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryRequest {

    private Long orderId;

    // 택배사 이름
    private String deliveryName;

    // 송장번호
    private Integer trackingNumber;

}
