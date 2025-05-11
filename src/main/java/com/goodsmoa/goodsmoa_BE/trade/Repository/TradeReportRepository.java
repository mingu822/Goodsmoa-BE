package com.goodsmoa.goodsmoa_BE.trade.Repository;


import com.goodsmoa.goodsmoa_BE.trade.Entity.TradeReportEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeReportRepository extends JpaRepository<TradeReportEntity, Long> {
    Page<TradeReportEntity> findAllByUser(UserEntity user, Pageable pageable);
}
