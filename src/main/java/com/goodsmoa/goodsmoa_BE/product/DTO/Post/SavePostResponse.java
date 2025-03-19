package com.goodsmoa.goodsmoa_BE.product.DTO.Post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavePostResponse {

    private long id;

    private String title;

    private String content;

    private String thumbnailImage;

    private Integer categoryId;


}
