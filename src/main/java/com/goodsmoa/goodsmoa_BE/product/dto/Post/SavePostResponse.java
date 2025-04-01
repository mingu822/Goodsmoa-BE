package com.goodsmoa.goodsmoa_BE.product.dto.Post;

import lombok.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SavePostResponse {

    private long id;

    private String title;

    private String content;

    private String thumbnailImage;

    private Integer categoryId;


}
