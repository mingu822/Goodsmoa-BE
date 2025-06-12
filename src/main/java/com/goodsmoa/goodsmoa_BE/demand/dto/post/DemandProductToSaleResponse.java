package com.goodsmoa.goodsmoa_BE.demand.dto.post;

import lombok.Builder;
import lombok.Getter;

@Builder @Getter
public class DemandProductToSaleResponse {

    private String name;  // 상품명

    private int price;  // 가격

    private String imageUrl;  // 이미지
}
