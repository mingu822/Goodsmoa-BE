package com.goodsmoa.goodsmoa_BE.demand.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Builder @Getter
public class DemandProductRequest {

    @NotBlank(message = "상품명은 필수입니다.")
    @Size(max=50)
    private String name;  // 상품명

    @NotBlank(message = "가격은 필수입니다.")
    private int price;  // 가격

    @NotBlank(message = "상품별 이미지를 첨부해주세요")
    @Size(max=255)
    private String image;  // 이미지

    @NotBlank(message = "목표 수량은 필수입니다.")
    private int targetCount;  // 목표 수량
}
