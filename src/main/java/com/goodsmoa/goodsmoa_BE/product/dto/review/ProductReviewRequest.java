package com.goodsmoa.goodsmoa_BE.product.dto.review;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductReviewRequest {

    private Long postId;

    private Double rating;

    private String content;

}
