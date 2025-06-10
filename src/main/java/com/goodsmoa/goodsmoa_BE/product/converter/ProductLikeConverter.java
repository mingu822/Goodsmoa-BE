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

    public ProductLikeResponse toResponse(ProductPostEntity entity, UserEntity user) {
        return ProductLikeResponse.builder()
                .id(entity.getId())
                .views(entity.getViews())
                .title(entity.getTitle())
                .hashtag(entity.getHashtag())
                .thumbnailImage(entity.getThumbnailImage())
                .userId(user.getId())
                .userImage(user.getImage())
                .userNickName(user.getNickname())
                .build();
    }

}
