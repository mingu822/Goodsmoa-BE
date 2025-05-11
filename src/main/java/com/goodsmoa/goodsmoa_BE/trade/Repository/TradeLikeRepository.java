package com.goodsmoa.goodsmoa_BE.trade.Repository;

import com.goodsmoa.goodsmoa_BE.trade.Entity.TradeLikeEntity;
import com.goodsmoa.goodsmoa_BE.trade.Entity.TradePostEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;


import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TradeLikeRepository extends JpaRepository<TradeLikeEntity, Long> {
    Optional<TradeLikeEntity> findByTradeAndUser(TradePostEntity trade, UserEntity user);
    boolean existsByTradeAndUser(TradePostEntity trade, UserEntity user);

//    List<TradeLikeEntity> findAllByUser(UserEntity user);

    Page<TradeLikeEntity> findAllByUser(UserEntity user, Pageable pageable);

}
