package com.goodsmoa.goodsmoa_BE.product.Converter;

import com.goodsmoa.goodsmoa_BE.product.DTO.Delivery.ProductDeliveryRequest;
import com.goodsmoa.goodsmoa_BE.product.DTO.Delivery.ProductDeliveryResponse;
import com.goodsmoa.goodsmoa_BE.product.Entity.ProductDeliveryEntity;
import com.goodsmoa.goodsmoa_BE.product.Entity.ProductPostEntity;
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
