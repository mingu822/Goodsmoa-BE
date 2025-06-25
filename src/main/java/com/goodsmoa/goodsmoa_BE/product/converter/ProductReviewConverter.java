package com.goodsmoa.goodsmoa_BE.product.converter;

import com.goodsmoa.goodsmoa_BE.product.dto.review.ProductReviewRequest;
import com.goodsmoa.goodsmoa_BE.product.dto.review.ProductSummaryResponse;
import com.goodsmoa.goodsmoa_BE.product.entity.ProductPostEntity;
import com.goodsmoa.goodsmoa_BE.product.entity.ProductReviewEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ProductReviewConverter {

    public ProductSummaryResponse toResponse(ProductPostEntity product){
        return ProductSummaryResponse.builder()
                .productId(product.getId())
                .title(product.getTitle())
                .thumbnail(product.getThumbnailImage())
                .build();
    }

    public ProductReviewEntity toEntity(ProductReviewRequest request, ProductPostEntity postEntity, UserEntity user){
        return ProductReviewEntity.builder()
                .productPostEntity(postEntity)
                .rating(request.getRating())
                .user(user)
                .content(request.getContent())
                .build();
    }

}
