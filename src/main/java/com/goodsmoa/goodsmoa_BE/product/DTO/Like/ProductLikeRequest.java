package com.goodsmoa.goodsmoa_BE.product.DTO.Like;

import com.goodsmoa.goodsmoa_BE.product.Entity.ProductLikeEntity;
import com.goodsmoa.goodsmoa_BE.product.Entity.ProductPostEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Builder
public class ProductLikeRequest {

    private Long id; // ✅ 좋아요 ID (PK)

    @NotNull(message = "상품 게시글 ID는 필수입니다.")
    private Long productPostId; // ✅ ProductPost의 FK

    @NotNull(message = "사용자 ID는 필수입니다.")
    private Long userId; // ✅ User의 FK

    /** ✅ DTO → 엔티티 변환 */
    public ProductLikeEntity toEntity(ProductPostEntity productPostEntity, UserEntity user) {
        return ProductLikeEntity.builder()
                .id(id)
                .productPostEntity(productPostEntity) // ✅ FK 매핑
                .user(user) // ✅ FK 매핑
                .build();
    }

    /** ✅ 엔티티 → DTO 변환 */
    public static ProductLikeRequest toRequest(ProductLikeEntity productLikeEntity) {
        return ProductLikeRequest.builder()
                .id(productLikeEntity.getId()) // ✅ PK 반환
                .productPostId(productLikeEntity.getProductPostEntity().getId()) // ✅ ProductPost FK
                .userId(Long.valueOf(productLikeEntity.getUser().getId())) // ✅ User FK
                .build();
    }
}
