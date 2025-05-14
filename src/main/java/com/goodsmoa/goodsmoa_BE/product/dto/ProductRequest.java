package com.goodsmoa.goodsmoa_BE.product.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequest {

    private Long id;

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

    @NotNull(message = "최대 구매 가능 수량은 필수입니다.")
    @Min(value = 1, message = "최대 구매 가능 수량은 1개 이상이어야 합니다.")
    private Integer maxQuantity;

    private Long postId;

}
