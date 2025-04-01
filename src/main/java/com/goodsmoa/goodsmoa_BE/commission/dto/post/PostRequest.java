package com.goodsmoa.goodsmoa_BE.commission.dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostRequest {

    private Long id;

    private String title;

    private Boolean type;

    private String content;

    private String thumbnailImage;

    private Integer requestLimited;

    private Integer minimumPrice;

    private Integer maximumPrice;

    private String hashtag;
}
