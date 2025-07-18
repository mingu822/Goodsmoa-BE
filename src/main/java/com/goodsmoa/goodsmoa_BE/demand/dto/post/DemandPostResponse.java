package com.goodsmoa.goodsmoa_BE.demand.dto.post;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Builder @Getter
public class DemandPostResponse {
    // 수요조사글 구성요소
    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private String hashtag;
    private boolean state;
    private Long views;
    private Long likes;
    private String category;
    private Long userOrderId;
    private Boolean likeStatus;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime endTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;

    // 작성자 정보
    private String userId;
    private String userName;
    private String userImage;
    private String userContent;

    // 수요조사 제품 리스트
    private List<DemandProductResponse> products;
}
