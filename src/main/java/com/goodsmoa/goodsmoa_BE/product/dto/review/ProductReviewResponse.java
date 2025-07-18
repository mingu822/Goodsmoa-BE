package com.goodsmoa.goodsmoa_BE.product.dto.review;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ProductReviewResponse {

    private Long reviewId;

    private Long postId;

    private Double rating;

    private String content;

    private LocalDateTime createdAt;

    private List<ProductReviewMediaResponse> media;
}