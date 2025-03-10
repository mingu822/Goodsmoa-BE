package com.goodsmoa.goodsmoa_BE.trade.DTO;



import com.goodsmoa.goodsmoa_BE.trade.Entity.TradePost;
import com.goodsmoa.goodsmoa_BE.trade.Entity.TradeReport;
import com.goodsmoa.goodsmoa_BE.user.Entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TradeReportResponse {

    private Long id;
    private TradePost tradeId;
    private User userId;
    private String reason;
    private String status;

    // 엔티티 객체를 DTO로 변환하는 생성자
    public TradeReportResponse(TradeReport tradeReportEntity , User user) {
        this.id = tradeReportEntity.getId();
        this.tradeId = tradeReportEntity.getTrade();  // TradeEntity의 id
        this.userId = tradeReportEntity.getUser();  // UserEntity의 id
        this.reason = tradeReportEntity.getReason();
        this.status = tradeReportEntity.getStatus();
    }
}
