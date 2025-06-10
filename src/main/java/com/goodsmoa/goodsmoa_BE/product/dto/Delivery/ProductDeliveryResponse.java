package com.goodsmoa.goodsmoa_BE.product.dto.delivery;

import lombok.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDeliveryResponse {

    private Long id;

    private String name;

    private Integer price;
}
