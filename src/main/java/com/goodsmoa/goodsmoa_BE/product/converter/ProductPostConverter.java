package com.goodsmoa.goodsmoa_BE.product.converter;

import com.goodsmoa.goodsmoa_BE.category.Entity.Category;
import com.goodsmoa.goodsmoa_BE.product.dto.Post.*;
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
     * save DTO -> Entity 변경
     * 상품을 담기 위해 DB에 임시저장하기 위한 메서드
     */
    public ProductPostEntity saveToEntity(SavePostRequest request, UserEntity user, Category category) {
        return ProductPostEntity.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .thumbnailImage(request.getThumbnailImage())
                .createdAt(LocalDateTime.now())
                .state(false)
                .views(0L)
                .user(user)
                .category(category)
                .build();
    }

    /**
     *  save 엔티티 → DTO 변환
     *  임시저장 후 이어서 상품을 담기 위해 정보를 반환하는 메서드
     */
    public SavePostResponse saveToResponse(ProductPostEntity entity){
        return SavePostResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .thumbnailImage(entity.getThumbnailImage())
                .categoryId(entity.getCategory().getId())
                .build();
    }

    /**
     *  create 엔티티 → DTO 변환
     *  저장 후 반환 시키기 위한 메서드
     */
    public PostResponse createToResponse(ProductPostEntity entity) {
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
                .categoryName(entity.getCategory().getName()) // ✅ 카테고리 이름만 반환
                .userName(entity.getUser().getName())   // ✅ 작성자 아이디 반환
                .build();
    }
    /**
     *  조회를 위한 Entity -> DTO
     *
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
                .user(entity.getUser())
                .views(entity.getViews())
                .products(products.stream().map(productConverter::toResponse).toList())
                .delivers(delivers.stream().map(productDeliveryConverter::toResponse).toList())
                .build();
    }
}
