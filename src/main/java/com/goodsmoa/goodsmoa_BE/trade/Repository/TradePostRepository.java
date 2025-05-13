package com.goodsmoa.goodsmoa_BE.trade.Repository;

import com.goodsmoa.goodsmoa_BE.trade.Entity.TradePostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface TradePostRepository extends JpaRepository<TradePostEntity, Long> {
    Page<TradePostEntity> findByIdNotIn(List<Long> ids, Pageable pageable);
}
