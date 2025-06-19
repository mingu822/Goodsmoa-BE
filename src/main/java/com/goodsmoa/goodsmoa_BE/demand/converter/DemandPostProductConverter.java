package com.goodsmoa.goodsmoa_BE.demand.converter;

import com.goodsmoa.goodsmoa_BE.demand.dto.post.DemandPostProductRequest;
import com.goodsmoa.goodsmoa_BE.demand.dto.post.DemandProductResponse;
import com.goodsmoa.goodsmoa_BE.demand.dto.post.DemandProductToSaleResponse;
import com.goodsmoa.goodsmoa_BE.demand.entity.DemandOrderProductEntity;
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
        float achievementRate = ((float) entity.getOrderCount() / entity.getTargetCount()) * 100;
        return DemandProductResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .price(entity.getPrice())
                .imageUrl(entity.getImageUrl())
                .defaultValue(0)
                .targetCount(entity.getTargetCount())
                .orderCount(entity.getOrderCount())
                .achievementRate(achievementRate)
                .build();
    }
    // 참여했다면 초기값 추가
    public DemandProductResponse toResponse(DemandPostProductEntity entity, int orderedQuantity) {
        float achievementRate = ((float) entity.getOrderCount() / entity.getTargetCount()) * 100;
        return DemandProductResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .price(entity.getPrice())
                .imageUrl(entity.getImageUrl())
                .defaultValue(orderedQuantity)
                .targetCount(entity.getTargetCount())
                .orderCount(entity.getOrderCount())
                .achievementRate(achievementRate)
                .build();
    }

    public DemandProductToSaleResponse toSaleResponse(DemandPostProductEntity entity) {
        return DemandProductToSaleResponse.builder()
                .name(entity.getName())
                .price(entity.getPrice())
                .imageUrl(entity.getImageUrl())
                .build();
    }
}
