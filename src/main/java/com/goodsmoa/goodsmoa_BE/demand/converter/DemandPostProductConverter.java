package com.goodsmoa.goodsmoa_BE.demand.converter;

import com.goodsmoa.goodsmoa_BE.demand.dto.post.DemandPostProductRequest;
import com.goodsmoa.goodsmoa_BE.demand.dto.post.DemandProductResponse;
import com.goodsmoa.goodsmoa_BE.demand.entity.DemandPostEntity;
import com.goodsmoa.goodsmoa_BE.demand.entity.DemandPostProductEntity;
import org.springframework.stereotype.Component;

@Component
public class DemandPostProductConverter {

    // 생성할때만 사용
    // DemandPostProductRequest -> DemandPostProductEntity
    public DemandPostProductEntity toEntity(DemandPostEntity postEntity, DemandPostProductRequest request) {
        return DemandPostProductEntity.builder()
                .name(request.getName())
                .price(request.getPrice())
                .imageUrl(request.getImageUrl())
                .targetCount(request.getTargetCount())
                .demandPostEntity(postEntity)
                .build();
    }

    // DemandProductEntity -> DemandProductEntityResponse
    public DemandProductResponse toResponse(DemandPostProductEntity entity) {
        return DemandProductResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .price(entity.getPrice())
                .imageUrl(entity.getImageUrl())
                .targetCount(entity.getTargetCount())
                .build();
    }
}
