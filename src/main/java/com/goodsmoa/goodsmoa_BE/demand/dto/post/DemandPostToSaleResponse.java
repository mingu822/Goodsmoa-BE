package com.goodsmoa.goodsmoa_BE.demand.dto.post;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Builder @Getter
public class DemandPostToSaleResponse {
    // 수요조사글 구성요소
    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private String hashtag;
    private Integer category;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime endTime;

    // 수요조사 제품 리스트
    private List<DemandProductToSaleResponse> products;
}
