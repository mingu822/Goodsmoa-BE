package com.goodsmoa.goodsmoa_BE.product.dto;

import lombok.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private long id;

    private String name;

    private Integer price;

    private Integer quantity;

    private String image;

    private Integer maxQuantity;

    private Long postId;

}
