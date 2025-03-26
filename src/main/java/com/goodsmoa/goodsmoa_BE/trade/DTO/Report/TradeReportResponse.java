package com.goodsmoa.goodsmoa_BE.trade.DTO.Report;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TradeReportResponse {

    private String content;

    private String title;
}

