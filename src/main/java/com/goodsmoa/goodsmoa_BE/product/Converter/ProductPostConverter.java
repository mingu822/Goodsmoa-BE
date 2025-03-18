package com.goodsmoa.goodsmoa_BE.product.Converter;

import com.goodsmoa.goodsmoa_BE.category.Entity.Category;
import com.goodsmoa.goodsmoa_BE.product.DTO.Post.*;
import com.goodsmoa.goodsmoa_BE.product.Entity.ProductEntity;
import com.goodsmoa.goodsmoa_BE.product.Entity.ProductPostEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class ProductPostConverter {

    @Autowired
    private  ProductConverter productConverter;

    /**
     * save DTO -> Entity 변경
     * 상품을 담기 위해 DB에 임시저장하기 위한 메서드
     */
    public ProductPostEntity saveToEntity(SavePostRequest request, User user) {
        log.info("RequestContent : "+ request.getContent());
        return ProductPostEntity.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .thumbnailImage(request.getThumbnailImage())
                .createdAt(LocalDateTime.now())
                .state(false)
                .views(0L)
                .user(user)
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
                .build();
    }

    /**
     * create DTO -> Entity 변경
     * 상품까지 담은 후 DB에 저장을 위한 메서드
     */
    public ProductPostEntity createToEntity(PostRequest request, Category category, User user) {
        return ProductPostEntity.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .thumbnailImage(request.getThumbnailImage())
                .isPublic(request.getIsPublic())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .state(request.getState())
                .password(request.getPassword())
                .hashtag(request.getHashtag())
                .category(category)
                .user(user)
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
                .userId(entity.getUser().getName())   // ✅ 작성자 이름만 반환
                .build();
    }

    public PostDetailResponse detailToResponse(List<ProductEntity> products, ProductPostEntity entity){
        return PostDetailResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .hashtag(entity.getHashtag())
                .categoryName(entity.getCategory().getName())
                .user(entity.getUser())
                .products(products.stream().map(productConverter::toResponse).toList())
                .build();
    }
}