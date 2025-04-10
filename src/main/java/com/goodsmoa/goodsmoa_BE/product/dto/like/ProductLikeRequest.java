package com.goodsmoa.goodsmoa_BE.product.dto.Like;

import com.goodsmoa.goodsmoa_BE.product.entity.ProductLikeEntity;
import com.goodsmoa.goodsmoa_BE.product.entity.ProductPostEntity;
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
}
