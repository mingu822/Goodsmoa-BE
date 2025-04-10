package com.goodsmoa.goodsmoa_BE.product.dto.Post;

import com.goodsmoa.goodsmoa_BE.product.dto.Delivery.ProductDeliveryResponse;
import com.goodsmoa.goodsmoa_BE.product.dto.ProductResponse;
import com.goodsmoa.goodsmoa_BE.product.entity.ProductDeliveryEntity;
import com.goodsmoa.goodsmoa_BE.product.entity.ProductEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {

    private Long id;  // ✅ ProductPost 의 PK (게시글 ID)

    private String title;

    private String content;

    private LocalDateTime createdAt;

    private String thumbnailImage;

    private Boolean isPublic;

    private LocalDate startTime;

    private LocalDate endTime;

    private Boolean state;

    private String password;

    private Long views;

    private String hashtag;

    private String categoryName;

    private UserEntity user;

    private List<ProductResponse> products;

    private List<ProductDeliveryResponse> delivers;

}
