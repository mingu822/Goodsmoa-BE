package com.goodsmoa.goodsmoa_BE.search.dto;

import lombok.Builder;
import lombok.Getter;

@Builder @Getter
public class SearchDocWithUserResponse {
    // 식별용
    private final String id;
    private final String boardType;

    // 작성글 정보
    private final String title;
    private final String hashtag;
    private final String thumbnailUrl;
    private final Long views;

    // 작성자 정보
    private final String nickname;
    private final String profileUrl;
}
