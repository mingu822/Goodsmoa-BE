package com.goodsmoa.goodsmoa_BE.demand.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Builder @Getter
public class DemandProductResponse {

    private Long id;

    private String name;  // 상품명

    private int price;  // 가격

    private String imageUrl;  // 이미지

    private int targetCount;  // 목표 수량
}
