package com.goodsmoa.goodsmoa_BE.product.converter;

import com.goodsmoa.goodsmoa_BE.category.Entity.Category;

import com.goodsmoa.goodsmoa_BE.product.dto.post.PostDetailResponse;
import com.goodsmoa.goodsmoa_BE.product.dto.post.PostRequest;
import com.goodsmoa.goodsmoa_BE.product.dto.post.PostResponse;
import com.goodsmoa.goodsmoa_BE.product.dto.post.PostsResponse;
import com.goodsmoa.goodsmoa_BE.product.entity.ProductDeliveryEntity;
import com.goodsmoa.goodsmoa_BE.product.entity.ProductEntity;
import com.goodsmoa.goodsmoa_BE.product.entity.ProductPostEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Component
public class ProductPostConverter {

    private final ProductConverter productConverter;

    private final ProductDeliveryConverter productDeliveryConverter;

    /**
     * create DTO -> entity 변경
     * 상품을 담기 위해 DB에 임시저장하기 위한 메서드
     */
    public ProductPostEntity createToEntity(PostRequest request, UserEntity user, Category category) {
        return ProductPostEntity.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .thumbnailImage(request.getThumbnailImage())
                .isPublic(request.getIsPublic())
                .createdAt(LocalDateTime.now())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .state(false)
                .views(0L)
                .likes(0L)
                .password(request.getPassword())
                .hashtag(request.getHashtag())
                .user(user)
                .category(category)
                .build();
    }
    /**
     *  update 엔티티 → DTO 변환
     *  저장 후 반환 시키기 위한 메서드
     */
    public PostResponse createToResponse(ProductPostEntity entity, List<ProductEntity> products, List<ProductDeliveryEntity> delivers) {
        return PostResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .createdAt(entity.getCreatedAt())
                .thumbnailImage(entity.getThumbnailImage())
                .isPublic(entity.getIsPublic())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .state(entity.getState())
                .views(entity.getViews())
                .hashtag(entity.getHashtag())
                .categoryName(entity.getCategory().getName())
                .user(entity.getUser())
                .products(products.stream().map(productConverter::toResponse).toList())
                .delivers(delivers.stream().map(productDeliveryConverter::toResponse).toList())
                .build();
    }
    /**
     *  조회를 위한 entity -> DTO
     */
    public PostDetailResponse detailToResponse(List<ProductEntity> products, List<ProductDeliveryEntity>  delivers, ProductPostEntity entity){
        return PostDetailResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .hashtag(entity.getHashtag())
                .categoryName(entity.getCategory().getName())
                .userId(entity.getUser().getId())
                .thumbnailImage(entity.getThumbnailImage())
                .nickname(entity.getUser().getNickname())
                .userImage(entity.getUser().getImage())
                .views(entity.getViews())
                .products(products.stream().map(productConverter::toResponse).toList())
                .delivers(delivers.stream().map(productDeliveryConverter::toResponse).toList())
                .build();
    }

    public PostsResponse toPostsResponse(ProductPostEntity entity) {
        return PostsResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .createdAt(entity.getCreatedAt())
                .thumbnailImage(entity.getThumbnailImage())
                .views(entity.getViews())
                .hashtag(entity.getHashtag())
                .userId(entity.getUser().getId())
                .userNickName(entity.getUser().getNickname())
                .userImage(entity.getUser().getImage())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .build();
    }

}
