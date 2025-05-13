package com.goodsmoa.goodsmoa_BE.trade.Repository;

import com.goodsmoa.goodsmoa_BE.trade.Entity.TradePostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface TradePostRepository extends JpaRepository<TradePostEntity, Long> {
    List<TradePostEntity> findAllByOrderByCreatedAtDescPulledAtDesc();

    List<TradePostEntity> findAllByIdNotIn(List<Long> hiddenPostIds);
}
