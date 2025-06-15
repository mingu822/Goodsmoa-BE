package com.goodsmoa.goodsmoa_BE.demand.dto.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder @Getter
public class DemandOrderListResponse {
    // 주문 ID
    private Long id;

    private String title;

    private String imageUrl;

    // 주문 생성일
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;
}
