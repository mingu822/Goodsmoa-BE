package com.goodsmoa.goodsmoa_BE.product.dto.review;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ProductSummaryResponse {

    // 각 상품글 리뷰 목록

    private Long reviewId;

    private Long productId;

    private String userId;

    private String userName;

    private Double rating;

    private String content;

    private LocalDateTime createdAt;

    private List<String> mediaUrls;

}