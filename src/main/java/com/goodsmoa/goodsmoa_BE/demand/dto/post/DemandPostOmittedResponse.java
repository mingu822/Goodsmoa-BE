package com.goodsmoa.goodsmoa_BE.demand.dto.post;

import lombok.Builder;
import lombok.Getter;


@Builder @Getter
public class DemandPostOmittedResponse {
    // 수요조사글 구성요소
    private Long id;
    private String title;
    private String imageUrl;
    private String hashtag;
    private Long views;
    private String category;
    private String state;
}
