package com.goodsmoa.goodsmoa_BE.commission.dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponse {

    private Long id;

    private String title;

    private String thumbnailImage;

    private String userName;

    private String userImage;

    private Long views;

    private String hashtag;

}
