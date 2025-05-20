package com.goodsmoa.goodsmoa_BE.demand.converter;

import com.goodsmoa.goodsmoa_BE.category.Entity.Category;
import com.goodsmoa.goodsmoa_BE.demand.dto.post.DemandPostCreateRequest;
import com.goodsmoa.goodsmoa_BE.demand.dto.post.DemandPostListResponse;
import com.goodsmoa.goodsmoa_BE.demand.dto.post.DemandPostResponse;
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
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .imageUrl(request.getImageUrl())
                .hashtag(request.getHashtag())
                .state(true)
                .views(0L)
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
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .imageUrl(entity.getImageUrl())
                .state(entity.isState())
                .views(entity.getViews())
                .hashtag(entity.getHashtag())
                .category(entity.getCategory().getName())
                .createdAt(entity.getCreatedAt())
                .products(entity.getProducts().stream().map(demandPostProductConverter::toResponse).toList())
                .userId(user.getId())
                .userName(user.getName())
                .userImage(user.getImage())
                .userContent(user.getContent())
                .build();
    }

    // DemandPostEntity -> DemandPostListResponse
    public DemandPostListResponse toListResponse(DemandPostEntity entity) {
        UserEntity user = entity.getUser();

        return DemandPostListResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .views(entity.getViews())
                .hashtag(entity.getHashtag())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .userId(user.getId())
                .userNickName(user.getNickname())
                .userImage(user.getImage())
                .build();
    }
}
