package com.goodsmoa.goodsmoa_BE.demand.dto.post;

import lombok.Builder;
import lombok.Getter;

@Builder @Getter
public class DemandProductResponse {

    private Long id;

    private String name;  // 상품명

    private int price;  // 가격

    private String imageUrl;  // 이미지

    private int targetCount;  // 목표 수량
    
    private int orderCount; // 주문 수량
    
    private float achievementRate; // 달성도
}
