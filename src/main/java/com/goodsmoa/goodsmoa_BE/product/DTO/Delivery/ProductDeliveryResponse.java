package com.goodsmoa.goodsmoa_BE.product.DTO.Delivery;

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
