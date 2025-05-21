package com.goodsmoa.goodsmoa_BE.demand.dto.post;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder @Getter
public class DemandPostListResponse {
    // 수요조사글 목록에 보이는 것들
    private Long id;
    private String title;
    private Long views;
    private String hashtag;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String userId;
    private String userNickName;
    private String userImage;
}
