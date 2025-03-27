package com.goodsmoa.goodsmoa_BE.demand.converter;

import com.goodsmoa.goodsmoa_BE.demand.dto.post.DemandProductResponse;
import com.goodsmoa.goodsmoa_BE.demand.entity.DemandPostEntity;
import com.goodsmoa.goodsmoa_BE.demand.entity.DemandProductEntity;
import org.springframework.stereotype.Component;

@Component
public class DemandProductConverter {

    // DemandProductEntity -> DemandProductEntityResponse
    public DemandProductResponse toResponse(DemandProductEntity entity) {
        return DemandProductResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .price(entity.getPrice())
                .image(entity.getImage())
                .targetCount(entity.getTargetCount())
                .build();
    }

    // 생성할때만 사용
    // DemandEntityRequest -> DemandProductEntity
    public DemandProductEntity toEntity(DemandPostEntity postEntity, DemandProductEntity productEntity) {
        return DemandProductEntity.builder()
                .name(productEntity.getName())
                .price(productEntity.getPrice())
                .image(productEntity.getImage())
                .targetCount(productEntity.getTargetCount())
                .demandPostEntity(postEntity)
                .build();
    }
}
