package com.goodsmoa.goodsmoa_BE.demand.dto.order;

import lombok.Builder;
import lombok.Getter;

@Builder @Getter
public class DemandOrderProductResponse {
    // 주문 상품 id(pk)
    private Long id;

    // 상품명
    private String name;

    // 상품 이미지
    private String image;

    // 구매 수량
    private int quantity;
}
