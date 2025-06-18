package com.goodsmoa.goodsmoa_BE.product.converter;

import com.goodsmoa.goodsmoa_BE.product.dto.like.ProductLikeResponse;
import com.goodsmoa.goodsmoa_BE.product.entity.ProductLikeEntity;
import com.goodsmoa.goodsmoa_BE.product.entity.ProductPostEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductLikeConverter {

    public ProductLikeEntity toEntity(ProductPostEntity entity, UserEntity user) {
        return ProductLikeEntity.builder()
                .productPostEntity(entity)
                .user(user)
                .build();
    }

    public ProductLikeResponse toResponse(ProductLikeEntity entity) {
        return ProductLikeResponse.builder()
                .id(entity.getId())
                .postId(entity.getProductPostEntity().getId())
                .views(entity.getProductPostEntity().getViews())
                .title(entity.getProductPostEntity().getTitle())
                .hashtag(entity.getProductPostEntity().getHashtag())
                .thumbnailImage(entity.getProductPostEntity().getThumbnailImage())
                .userId(entity.getUser().getId())
                .userImage(entity.getUser().getImage())
                .userNickName(entity.getUser().getNickname())
                .build();
    }
}
