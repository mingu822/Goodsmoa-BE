package com.goodsmoa.goodsmoa_BE.product.dto.delivery;

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
    private Integer price;

    private Long postId;

}