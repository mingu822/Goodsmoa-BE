package com.goodsmoa.goodsmoa_BE.trade.DTO;

import com.goodsmoa.goodsmoa_BE.trade.Entity.TradePost;
import com.goodsmoa.goodsmoa_BE.trade.Entity.TradeReport;
import com.goodsmoa.goodsmoa_BE.user.Entity.User;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TradeReportRequest {

    @NotNull(message = "거래 ID는 필수입니다.")
    private Long tradeId;

    @NotNull(message = "유저 ID는 필수입니다.")
    private Long userId;

    @NotNull(message = "신고 사유는 필수입니다.")
    @Size(max = 500, message = "신고 사유는 최대 500자까지 가능합니다.")
    private String reason;

    @NotNull(message = "상태는 필수입니다.")
    @Size(max = 20, message = "상태는 최대 20자까지 가능합니다.")
    private String status;

    public TradeReportRequest(Long tradeId, Long userId, String reason, String status) {
        this.tradeId = tradeId;
        this.userId = userId;
        this.reason = reason;
        this.status = status;
    }

    // 엔티티 변환 메서드
    public TradeReport toEntity(TradePost tradeEntity, User userEntity) {
        return TradeReport.builder()
                .trade(tradeEntity)
                .user(userEntity)
                .reason(reason)
                .status(status)
                .build();
    }

}

