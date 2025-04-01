package com.goodsmoa.goodsmoa_BE.product.converter;

import com.goodsmoa.goodsmoa_BE.product.dto.ProductRequest;
import com.goodsmoa.goodsmoa_BE.product.dto.ProductResponse;
import com.goodsmoa.goodsmoa_BE.product.entity.ProductEntity;
import com.goodsmoa.goodsmoa_BE.product.entity.ProductPostEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductConverter {

    public ProductEntity toEntity(ProductPostEntity postEntity, ProductRequest request) {
        return ProductEntity.builder()
                .name(request.getName())
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .image(request.getImage())
                .available(ProductEntity.AvailabilityStatus.valueOf(request.getAvailable())) // Enum 변환
                .maxQuantity(request.getMaxQuantity())
                .productPostEntity(postEntity) // ✅ FK 매핑
                .build();
    }

    /** ✅ 엔티티 → DTO 변환 */
    public ProductResponse toResponse(ProductEntity entity) {
        return ProductResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .price(entity.getPrice())
                .image(entity.getImage())
                .quantity(entity.getQuantity())
                .maxQuantity(entity.getMaxQuantity())
                .build();
    }
}
