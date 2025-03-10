package com.goodsmoa.goodsmoa_BE.trade.DTO;

import com.goodsmoa.goodsmoa_BE.trade.Entity.TradeLike;
import com.goodsmoa.goodsmoa_BE.trade.Entity.TradePost;
import com.goodsmoa.goodsmoa_BE.user.Entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TradeLikeResponse {

    private Long id;
    private TradePost tradeId;
    private User userId;

    // 엔티티 객체를 DTO로 변환하는 생성자
    public TradeLikeResponse(TradeLike tradeLikeEntity ,TradePost tradeId, User user) {
        this.id = tradeLikeEntity.getId();
        this.tradeId = tradeLikeEntity.getTrade();  // TradeEntity의 id
        this.userId = tradeLikeEntity.getUser();  // UserEntity의 id
    }
}
