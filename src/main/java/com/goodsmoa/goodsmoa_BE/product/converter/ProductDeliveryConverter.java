package com.goodsmoa.goodsmoa_BE.product.converter;

import com.goodsmoa.goodsmoa_BE.product.dto.delivery.ProductDeliveryRequest;
import com.goodsmoa.goodsmoa_BE.product.dto.delivery.ProductDeliveryResponse;
import com.goodsmoa.goodsmoa_BE.product.entity.ProductDeliveryEntity;
import com.goodsmoa.goodsmoa_BE.product.entity.ProductPostEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductDeliveryConverter {

    /** ✅ DTO → 엔티티 변환 */
    public ProductDeliveryEntity toEntity(ProductDeliveryRequest request, ProductPostEntity entity) {
        return ProductDeliveryEntity.builder()
                .id(request.getId())
                .name(request.getName())
                .price(request.getPrice())
                .productPostEntity(entity)
                .build();
    }

    /** ✅ 엔티티 → DTO 변환 */
    public ProductDeliveryResponse toResponse(ProductDeliveryEntity entity) {
        return ProductDeliveryResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .price(entity.getPrice())
                .build();
    }
}
