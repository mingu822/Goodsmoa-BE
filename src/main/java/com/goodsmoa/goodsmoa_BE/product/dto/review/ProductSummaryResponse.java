package com.goodsmoa.goodsmoa_BE.product.dto.review;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductSummaryResponse {

    // 리뷰 창에서 필요한 것들

    private Long productId;
    private String title;
    private String thumbnail;
}