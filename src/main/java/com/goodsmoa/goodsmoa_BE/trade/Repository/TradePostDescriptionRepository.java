package com.goodsmoa.goodsmoa_BE.trade.Repository;


import com.goodsmoa.goodsmoa_BE.trade.Entity.TradePostDescription;
import com.goodsmoa.goodsmoa_BE.trade.Entity.TradePostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradePostDescriptionRepository extends JpaRepository<TradePostDescription, Long> {

    void deleteByTradePost(TradePostEntity tradePost);
}
