package com.goodsmoa.goodsmoa_BE.trade.DTO.Report;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TradeReportRequest {


    @NotBlank(message = "신고 사유는 필수입니다.")
    @Size(max = 500, message = "신고 사유는 최대 500자까지 가능합니다.")
    private String content;

    @NotBlank(message = "신고 제목은 필수입니다.")
    @Size(max = 20, message = "제목은 최대 20자까지 가능합니다.")
    private String title;



}


