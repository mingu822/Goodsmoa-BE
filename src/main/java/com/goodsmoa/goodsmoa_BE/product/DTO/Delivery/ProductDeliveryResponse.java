package com.goodsmoa.goodsmoa_BE.product.DTO.Delivery;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDeliveryResponse {

    private Long id;

    private String name;

    private Integer price;
}
