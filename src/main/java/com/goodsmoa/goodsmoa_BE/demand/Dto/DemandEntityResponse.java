package com.goodsmoa.goodsmoa_BE.demand.Dto;

import com.goodsmoa.goodsmoa_BE.demand.Entity.DemandProductEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Builder @Getter
public class DemandEntityResponse {
    // 수요조사글 구성요소
    private Long id;
    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String image;
    private boolean state;
    private Long views;
    private String hashtag;
    private LocalDateTime creatAt;

    // 작성자 정보
    private String userId;
    private String userNickname;

    // 수요조사 제품 리스트
    private List<DemandProductEntity> products;
}
