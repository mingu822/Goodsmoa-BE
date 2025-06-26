package com.goodsmoa.goodsmoa_BE.trade.Repository;

import com.goodsmoa.goodsmoa_BE.trade.Entity.TradeImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TradeImageRepository extends JpaRepository<TradeImageEntity, Long > {

    /**
     * 거래글 안에 모든 이미지 조회
     */

    List<TradeImageEntity> findAllById(Long tradeImageEntityId);

    void deleteByImageUrl(String imageUrl);
}

