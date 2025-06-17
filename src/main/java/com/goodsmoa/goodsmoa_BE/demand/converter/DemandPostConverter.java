package com.goodsmoa.goodsmoa_BE.demand.converter;

import com.goodsmoa.goodsmoa_BE.category.Entity.Category;
import com.goodsmoa.goodsmoa_BE.demand.dto.post.DemandPostCreateRequest;
import com.goodsmoa.goodsmoa_BE.demand.dto.post.DemandPostOmittedResponse;
import com.goodsmoa.goodsmoa_BE.demand.dto.post.DemandPostResponse;
import com.goodsmoa.goodsmoa_BE.demand.dto.post.DemandPostToSaleResponse;
import com.goodsmoa.goodsmoa_BE.demand.entity.DemandPostEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Component
@AllArgsConstructor
public class DemandPostConverter {

    private final DemandPostProductConverter demandPostProductConverter;

    // 생성에만 쓰임. 수정에는 안쓰임
    // DemandEntityCreateRequest -> DemandPostEntity
    public DemandPostEntity toEntity(UserEntity user, Category category, DemandPostCreateRequest request) {
        return DemandPostEntity.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .hashtag(request.getHashtag())
                .state(true)
                .views(0L)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .createdAt(LocalDateTime.now())
                .user(user)
                .category(category)
                .products(new ArrayList<>())
                .build();
    }

    // DemandEntity -> DemandPostResponse
    public DemandPostResponse toResponse(DemandPostEntity entity) {
        UserEntity user = entity.getUser();

        return DemandPostResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .imageUrl(entity.getImageUrl())
                .hashtag(entity.getHashtag())
                .state(entity.isState())
                .views(entity.getViews())
                .category(entity.getCategory().getName())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .createdAt(entity.getCreatedAt())
                .userId(user.getId())
                .userName(user.getNickname())
                .userImage(user.getImage())
                .userContent(user.getContent())
                .products(entity.getProducts().stream().map(demandPostProductConverter::toResponse).toList())
                .build();
    }

    public DemandPostToSaleResponse toSaleResponse(DemandPostEntity entity) {
        return DemandPostToSaleResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .imageUrl(entity.getImageUrl())
                .hashtag(entity.getHashtag())
                .category(entity.getCategory().getId())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .products(entity.getProducts().stream().map(demandPostProductConverter::toSaleResponse).toList())
                .build();
    }

    public DemandPostOmittedResponse toOmittedResponse(DemandPostEntity entity) {
        return DemandPostOmittedResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .imageUrl(entity.getImageUrl())
                .hashtag(entity.getHashtag())
                .views(entity.getViews())
                .category(entity.getCategory().getName())
                .state(LocalDateTime.now().isBefore(entity.getEndTime()) ? "진행중" : "마감")
                .build();
    }
}
