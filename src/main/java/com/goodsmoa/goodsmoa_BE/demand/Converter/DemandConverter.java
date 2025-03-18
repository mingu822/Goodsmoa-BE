package com.goodsmoa.goodsmoa_BE.demand.Converter;

import com.goodsmoa.goodsmoa_BE.demand.Dto.DemandEntityRequest;
import com.goodsmoa.goodsmoa_BE.demand.Dto.DemandEntityResponse;
import com.goodsmoa.goodsmoa_BE.demand.Entity.DemandEntity;
import org.springframework.stereotype.Component;

@Component
public class DemandConverter {

    // DemandEntity -> DemandEntityResponse
    public DemandEntityResponse toResponse(DemandEntity entity) {
        return DemandEntityResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .image(entity.getImage())
                .state(entity.isState())
                .views(entity.getViews())
                .hashtag(entity.getHashtag())
                .creatAt(entity.getCreatedAt())
                .products(entity.getProducts())
                .userId(entity.getUser().getId())
                .userNickname(entity.getUser().getNickname())
                .build();
    }

    // DemandEntityRequest -> DemandEntity
    public DemandEntity toEntity(DemandEntityRequest request) {
        return DemandEntity.builder()
                .id(request.getId())
                .title(request.getTitle())
                .description(request.getDescription())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .image(request.getImage())
                .hashtag(request.getHashtag())
                .products(request.getProducts())
                .build();
    }
}
