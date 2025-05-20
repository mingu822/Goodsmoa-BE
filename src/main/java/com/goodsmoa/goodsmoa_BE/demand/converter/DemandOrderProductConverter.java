package com.goodsmoa.goodsmoa_BE.demand.converter;

import com.goodsmoa.goodsmoa_BE.demand.dto.order.DemandOrderProductRequest;
import com.goodsmoa.goodsmoa_BE.demand.dto.order.DemandOrderProductResponse;
import com.goodsmoa.goodsmoa_BE.demand.entity.DemandOrderEntity;
import com.goodsmoa.goodsmoa_BE.demand.entity.DemandOrderProductEntity;
import com.goodsmoa.goodsmoa_BE.demand.entity.DemandPostProductEntity;
import org.springframework.stereotype.Component;

@Component
public class DemandOrderProductConverter {

    // 생성할때만 사용
    // DemandEntityRequest -> DemandProductEntity
    public DemandOrderProductEntity toEntity(DemandOrderProductRequest request, DemandPostProductEntity postProductEntity, DemandOrderEntity orderEntity) {
        return DemandOrderProductEntity.builder()
                .quantity(request.getQuantity())
                .postProductEntity(postProductEntity)
                .demandOrderEntity(orderEntity)
                .build();
    }

    // DemandOrderProductEntity -> DemandOrderProductResponse
    public DemandOrderProductResponse toResponse(DemandOrderProductEntity entity) {
        return DemandOrderProductResponse.builder()
                .id(entity.getId())
                .name(entity.getPostProductEntity().getName())
                .image(entity.getPostProductEntity().getImageUrl())
                .quantity(entity.getQuantity())
                .build();
    }
}
