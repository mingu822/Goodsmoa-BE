package com.goodsmoa.goodsmoa_BE.product.DTO.Post;

import com.goodsmoa.goodsmoa_BE.product.DTO.Delivery.ProductDeliveryResponse;
import com.goodsmoa.goodsmoa_BE.product.DTO.ProductResponse;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostDetailResponse {

    private Long id;

    private String title;

    private String content;

    private LocalDate startTime;

    private LocalDate endTime;

    private String hashtag;

    private String categoryName;

    private UserEntity user;

    private Long views;

    private List<ProductResponse> products;

    private List<ProductDeliveryResponse> delivers;
}
