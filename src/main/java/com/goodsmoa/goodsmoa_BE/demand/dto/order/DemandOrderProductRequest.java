package com.goodsmoa.goodsmoa_BE.demand.dto.order;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Builder @Getter
public class DemandOrderProductRequest {

    @NotBlank(message = "구매 수량은 필수입니다.")
    private int quantity; // 구매 희망 개수

    @NotBlank(message = "orderEntity 가 없습니다")
    private Long orderId; // 주문서 ID

    @NotBlank(message = "상품이 존재하지 않습니다")
    private Long postProductId; // 본글 상품 ID
}
