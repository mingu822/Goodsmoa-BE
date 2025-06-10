package com.goodsmoa.goodsmoa_BE.product.dto.review;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductReviewRequest {

    private Long postId;

    private String title;

    private Double rating;

    private String content;

    // 나중에 이미지, 동영상 추가

}
