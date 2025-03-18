package com.goodsmoa.goodsmoa_BE.product.Converter;

import com.goodsmoa.goodsmoa_BE.product.DTO.ProductRequest;
import com.goodsmoa.goodsmoa_BE.product.DTO.ProductResponse;
import com.goodsmoa.goodsmoa_BE.product.Entity.ProductEntity;
import com.goodsmoa.goodsmoa_BE.product.Entity.ProductPostEntity;
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
