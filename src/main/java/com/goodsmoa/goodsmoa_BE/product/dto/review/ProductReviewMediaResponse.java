package com.goodsmoa.goodsmoa_BE.product.dto.review;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductReviewMediaResponse {
    private Long id;
    private String filePath;
}