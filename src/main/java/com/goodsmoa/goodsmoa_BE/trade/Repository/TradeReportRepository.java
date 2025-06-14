package com.goodsmoa.goodsmoa_BE.trade.Repository;


import com.goodsmoa.goodsmoa_BE.trade.Entity.TradePostEntity;
import com.goodsmoa.goodsmoa_BE.trade.Entity.TradeReportEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TradeReportRepository extends JpaRepository<TradeReportEntity, Long> {
    Page<TradeReportEntity> findAllByUser(UserEntity user, Pageable pageable);

    @Query("SELECT p FROM TradePostEntity p WHERE p.id NOT IN " +
            "(SELECT h.tradePost.id FROM UserHiddenPost h WHERE h.user = :user)")
    Page<TradePostEntity> findVisiblePostsForUser(@Param("user") UserEntity user, Pageable pageable);
}
