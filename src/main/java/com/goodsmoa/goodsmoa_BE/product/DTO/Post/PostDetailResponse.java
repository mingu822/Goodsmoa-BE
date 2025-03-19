package com.goodsmoa.goodsmoa_BE.product.DTO.Post;

import com.goodsmoa.goodsmoa_BE.product.DTO.Delivery.ProductDeliveryResponse;
import com.goodsmoa.goodsmoa_BE.product.DTO.ProductResponse;
import com.goodsmoa.goodsmoa_BE.user.Entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Builder
@Data
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

    private User user;

    private List<ProductResponse> products;

    private List<ProductDeliveryResponse> delivers;
}
