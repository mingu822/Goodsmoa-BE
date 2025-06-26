package com.goodsmoa.goodsmoa_BE.product.dto.review;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
public class ProductReviewDetailResponse {

    // 리뷰 디테일 dto

    private Long reviewId;
    private Long postId;
    private String userId;
    private String userName;
    private Double rating;
    private String content;
    private LocalDateTime createdAt;
    private List<String> mediaUrls;
}