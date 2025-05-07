package com.goodsmoa.goodsmoa_BE.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {        // 결제하기 창

    private Long orderId;

    private String postName;

    private String postThumbnail;

    private Integer productsPrice;

    private Integer deliveryPrice;

    private Integer totalPrice;

}
