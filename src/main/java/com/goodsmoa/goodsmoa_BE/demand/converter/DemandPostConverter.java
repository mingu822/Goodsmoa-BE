package com.goodsmoa.goodsmoa_BE.demand.converter;

import com.goodsmoa.goodsmoa_BE.category.Entity.Category;
import com.goodsmoa.goodsmoa_BE.demand.dto.post.DemandPostCreateRequest;
import com.goodsmoa.goodsmoa_BE.demand.dto.post.DemandPostListResponse;
import com.goodsmoa.goodsmoa_BE.demand.dto.post.DemandPostResponse;
import com.goodsmoa.goodsmoa_BE.demand.entity.DemandPostEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

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
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .image(request.getImage())
                .hashtag(request.getHashtag())
                .user(user)
                .category(category)
                .products(new ArrayList<>())
                .build();
    }

    // DemandEntity -> DemandPostResponse
    public DemandPostResponse toResponse(DemandPostEntity entity) {
        return DemandPostResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .image(entity.getImage())
                .state(entity.isState())
                .views(entity.getViews())
                .hashtag(entity.getHashtag())
                .category(entity.getCategory().getName())
                .creatAt(entity.getCreatedAt())
                .products(entity.getProducts().stream().map(demandPostProductConverter::toResponse).toList())
                .user(entity.getUser())
                .build();
    }

    // DemandPostEntity -> DemandPostListResponse
    public DemandPostListResponse toListResponse(DemandPostEntity entity) {
        return DemandPostListResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .views(entity.getViews())
                .hashtag(entity.getHashtag())
                .pulledAt(entity.getPulledAt())
                .userId(entity.getUser().getId())
                .userName(entity.getUser().getName())
                .userImage(entity.getUser().getImage())
                .build();
    }
}
