package com.goodsmoa.goodsmoa_BE.demand.dto.post;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder @Getter
@AllArgsConstructor
public class DemandPostProductRequest {

    @Nullable
    private Long id;

    @NotBlank(message = "상품명은 필수입니다")
    @Size(max=50)
    private String name;  // 상품명

    @NotNull(message = "가격은 필수입니다")
    private int price;  // 가격

    @NotBlank(message = "상품별 이미지를 첨부해주세요")
    @Size(max=255)
    private String imageUrl;  // 이미지

    private boolean imageUpdated; // 이미지 수정 여부

    @NotNull(message = "목표 수량은 필수입니다")
    private int targetCount;  // 목표 수량
}
