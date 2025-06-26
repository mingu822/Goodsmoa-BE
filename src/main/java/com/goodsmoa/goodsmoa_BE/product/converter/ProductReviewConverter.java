package com.goodsmoa.goodsmoa_BE.product.converter;

import com.goodsmoa.goodsmoa_BE.product.dto.review.*;
import com.goodsmoa.goodsmoa_BE.product.entity.ProductPostEntity;
import com.goodsmoa.goodsmoa_BE.product.entity.ProductReviewEntity;
import com.goodsmoa.goodsmoa_BE.product.entity.ProductReviewMediaEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Component
public class ProductReviewConverter {

    public ProductReviewDetailResponse toDetailResponse(ProductReviewEntity review, List<String> mediaUrls) {
        return ProductReviewDetailResponse.builder()
                .reviewId(review.getId())
                .postId(review.getProductPostEntity().getId())
                .userId(review.getUser().getId())
                .userName(review.getUser().getNickname())
                .rating(review.getRating())
                .content(review.getContent())
                .createdAt(review.getCreatedAt())
                .mediaUrls(mediaUrls)
                .build();
    }

    public ProductSummaryResponse toSummaryResponse(ProductReviewEntity review) {
        List<String> mediaUrls = review.getMediaList().stream()
                .map(media -> media.getFilePath())
                .toList();

        return ProductSummaryResponse.builder()
                .reviewId(review.getId())
                .userId(review.getUser().getId())
                .userName(review.getUser().getNickname())
                .rating(review.getRating())
                .content(review.getContent())
                .createdAt(review.getCreatedAt())
                .mediaUrls(mediaUrls)
                .build();
    }

//    public ProductSummaryResponse toResponse(ProductPostEntity product){
//        return ProductSummaryResponse.builder()
//                .productId(product.getId())
//                .title(product.getTitle())
//                .thumbnail(product.getThumbnailImage())
//                .build();
//    }

    public ProductReviewEntity toEntity(ProductReviewRequest request, ProductPostEntity postEntity, UserEntity user){
        return ProductReviewEntity.builder()
                .productPostEntity(postEntity)
                .rating(request.getRating())
                .user(user)
                .content(request.getContent())
                .build();
    }

    public ProductReviewResponse toMyReviewResponse(ProductReviewEntity review) {
        ProductPostEntity post = review.getProductPostEntity();

        List<ProductReviewMediaResponse> media = review.getMediaList().stream()
                .map(m -> new ProductReviewMediaResponse(m.getId(), m.getFilePath()))
                .toList();

        return ProductReviewResponse.builder()
                .reviewId(review.getId())
                .postId(post.getId())
                .rating(review.getRating())
                .content(review.getContent())
                .createdAt(review.getCreatedAt())
                .media(media)
                .build();
    }
}
