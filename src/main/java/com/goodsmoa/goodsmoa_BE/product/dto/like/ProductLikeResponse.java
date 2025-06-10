package com.goodsmoa.goodsmoa_BE.product.dto.like;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductLikeResponse {

    private Long id;

    private String title;

    private String thumbnailImage;

    private Long views;

    private String hashtag;

    private String userId;

    private String userNickName;

    private String userImage;

}
