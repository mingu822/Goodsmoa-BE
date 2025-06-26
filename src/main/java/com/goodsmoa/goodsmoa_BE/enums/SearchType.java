package com.goodsmoa.goodsmoa_BE.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SearchType {
    ALL("통합검색"),
    TITLE("제목검색"),
    DESCRIPTION("내용검색"),
    HASHTAG("해시태그검색");

    private final String description;
}
