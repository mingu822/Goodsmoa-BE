package com.goodsmoa.goodsmoa_BE.product.dto.post;

import com.goodsmoa.goodsmoa_BE.product.dto.delivery.ProductDeliveryResponse;
import com.goodsmoa.goodsmoa_BE.product.dto.ProductResponse;
import com.goodsmoa.goodsmoa_BE.product.dto.review.ProductReviewResponse;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostDetailResponse {

    private Long id;

    private String title;

    private String content;

    private LocalDate startTime;

    private LocalDate endTime;

    private String hashtag;

    private String categoryName;

    private String nickname;

    private String userImage;

    private String thumbnailImage;

    private String userId;

    private Long views;

    private List<ProductResponse> products;

    private List<ProductDeliveryResponse> delivers;

    private List<ProductReviewResponse> reviews;
}
