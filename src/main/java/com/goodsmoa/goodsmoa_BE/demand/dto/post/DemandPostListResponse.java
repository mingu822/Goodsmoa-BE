package com.goodsmoa.goodsmoa_BE.demand.dto.post;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public class DemandPostListResponse {
    // 수요조사글 목록에 보이는 것들
    private Long id;
    private String title;
    private Long views;
    private String hashtag;
    private LocalDateTime pulledAt;
    private String userId;
    private String userName;
    private String userImage;
}
