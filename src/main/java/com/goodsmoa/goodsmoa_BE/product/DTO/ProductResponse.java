package com.goodsmoa.goodsmoa_BE.product.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private long id;

    private String name;

    private Integer price;

    private Integer quantity;

    private String image;

    private Integer maxQuantity;

}
