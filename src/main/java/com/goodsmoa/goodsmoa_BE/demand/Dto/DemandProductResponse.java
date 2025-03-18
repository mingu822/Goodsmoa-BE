package com.goodsmoa.goodsmoa_BE.demand.Dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Builder @Getter
public class DemandProductResponse {
    @NotNull(message = "상품 ID는 필수입니다.")
    private Long id;  // 상품 ID

    @NotNull(message = "상품명은 필수입니다.")
    private String name;  // 상품명

    @NotNull(message = "가격은 필수입니다.")
    private int price;  // 가격

    private String image;  // 이미지

    @NotNull(message = "목표 수량은 필수입니다.")
    private int targetCount;  // 목표 수량
}
