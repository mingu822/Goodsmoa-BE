package com.goodsmoa.goodsmoa_BE.trade.DTO.Report;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TradeReportResponse {

    private Long id;
    private String userId;
    private String nickName;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private String tradePostTitle;
    private String imageUrl;
    private String status;

}

