package com.goodsmoa.goodsmoa_BE.product.DTO;

import com.goodsmoa.goodsmoa_BE.product.Entity.ProductEntity;
import com.goodsmoa.goodsmoa_BE.product.Entity.ProductPostEntity;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequest {

    @NotNull(message = "게시글 ID는 필수입니다.")
    private Long postId;  // ✅ ProductPost의 FK (게시글 ID)

    @NotBlank(message = "상품명은 필수입니다.")
    @Size(max = 40, message = "상품명은 최대 40자까지 가능합니다.")
    private String name;

    @NotNull(message = "가격은 필수입니다.")
    @Min(value = 100, message = "가격은 100원 이상이어야 합니다.")
    private Integer price;

    @NotNull(message = "수량은 필수입니다.")
    @Min(value = 0, message = "수량은 0개 이상이어야 합니다.")
    private Integer quantity;

    @NotBlank(message = "이미지 URL은 필수입니다.")
    @Size(max = 255, message = "이미지 URL은 최대 255자까지 가능합니다.")
    private String image;

    @NotBlank(message = "상품 상태는 필수입니다.")
    @Pattern(regexp = "판매중|품절|숨기기", message = "상품 상태는 '판매중', '품절', '숨기기' 중 하나여야 합니다.")
    private String available;

    @NotNull(message = "최대 구매 가능 수량은 필수입니다.")
    @Min(value = 1, message = "최대 구매 가능 수량은 1개 이상이어야 합니다.")
    private Integer maxQuantity;

}
