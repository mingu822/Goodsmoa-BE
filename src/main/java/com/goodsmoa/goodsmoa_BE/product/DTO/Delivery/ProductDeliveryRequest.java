package com.goodsmoa.goodsmoa_BE.product.DTO.Delivery;

import com.goodsmoa.goodsmoa_BE.product.Entity.ProductDeliveryEntity;
import com.goodsmoa.goodsmoa_BE.product.Entity.ProductPostEntity;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Builder
public class ProductDeliveryRequest {

    private Long id;  // ✅ ProductDelivery의 PK (배송 정보 ID)

    @NotBlank(message = "배송 방법 이름은 필수입니다.")
    @Size(max = 30, message = "배송 방법 이름은 최대 30자까지 가능합니다.")
    private String name;

    @Min(value = 0, message = "배송 가격은 0 이상이어야 합니다.")
    private int price;

    @NotNull(message = "상품 게시글 ID는 필수입니다.")
    private Long productPostId;  // ✅ ProductPost의 FK (상품 게시글 ID)

    /** ✅ DTO → 엔티티 변환 */
    public ProductDeliveryEntity toEntity(ProductPostEntity productPostEntity) {
        return ProductDeliveryEntity.builder()
                .id(id) // ✅ ProductDelivery의 PK
                .name(name)
                .price(price)
                .productPostEntity(productPostEntity) // ✅ FK 매핑
                .build();
    }

    /** ✅ 엔티티 → DTO 변환 */
    public static ProductDeliveryRequest toRequest(ProductDeliveryEntity productDeliveryEntity) {
        return ProductDeliveryRequest.builder()
                .id(productDeliveryEntity.getId()) // ✅ ProductDelivery의 PK
                .name(productDeliveryEntity.getName())
                .price(productDeliveryEntity.getPrice())
                .productPostId(productDeliveryEntity.getProductPostEntity().getId()) // ✅ ProductPost의 FK
                .build();
    }
}