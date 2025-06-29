package com.goodsmoa.goodsmoa_BE.demand.converter;

import com.goodsmoa.goodsmoa_BE.category.Entity.Category;
import com.goodsmoa.goodsmoa_BE.demand.dto.post.*;
import com.goodsmoa.goodsmoa_BE.demand.entity.DemandOrderEntity;
import com.goodsmoa.goodsmoa_BE.demand.entity.DemandOrderProductEntity;
import com.goodsmoa.goodsmoa_BE.demand.entity.DemandPostEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
                .likes(0L)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .createdAt(LocalDateTime.now())
                .pulledAt(LocalDateTime.now())
                .user(user)
                .category(category)
                .products(new ArrayList<>())
                .build();
    }

    // DemandEntity -> DemandPostResponse
    // 로그인 하지 않은 유저
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
                .likes(entity.getLikes())
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
    // 로그인 한 유저 참여한 경우 반영+좋아요 여부
    public DemandPostResponse toResponse(DemandPostEntity entity, DemandOrderEntity orderEntity, Boolean likeStatus) {
        UserEntity user = entity.getUser();

        Long userOrderId = orderEntity!=null ? orderEntity.getId() : null;
        Map<Long, Integer> orderedCountMap = orderEntity != null ?
                orderEntity.getDemandOrderProducts().stream()
                .collect(Collectors.groupingBy(
                        op -> op.getPostProductEntity().getId(),
                        Collectors.summingInt(DemandOrderProductEntity::getQuantity)
                )) : Collections.emptyMap();

        List<DemandProductResponse> productResponses = entity.getProducts().stream()
                .map(product -> {
                    int orderedQuantity = orderedCountMap.getOrDefault(product.getId(), 0);
                    return demandPostProductConverter.toResponse(product, orderedQuantity);
                })
                .toList();

        return DemandPostResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .imageUrl(entity.getImageUrl())
                .hashtag(entity.getHashtag())
                .state(entity.isState())
                .views(entity.getViews())
                .likes(entity.getLikes())
                .category(entity.getCategory().getName())
                .userOrderId(userOrderId)
                .likeStatus(likeStatus != null && likeStatus)
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .createdAt(entity.getCreatedAt())
                .userId(user.getId())
                .userName(user.getNickname())
                .userImage(user.getImage())
                .userContent(user.getContent())
                .products(productResponses)
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
