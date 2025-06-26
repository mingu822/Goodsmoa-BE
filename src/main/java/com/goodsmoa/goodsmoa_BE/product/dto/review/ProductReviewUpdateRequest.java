package com.goodsmoa.goodsmoa_BE.product.dto.review;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ProductReviewUpdateRequest {

    private Long reviewId;

    private Long postId;

    private Double rating;

    private String content;

    private List<String> imageUrls;      // 유지할 기존 이미지 URL

    private List<Long> deletedImageIds;  // 삭제할 이미지 id
}

