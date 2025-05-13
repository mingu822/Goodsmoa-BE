package com.goodsmoa.goodsmoa_BE.trade.Repository;


import com.goodsmoa.goodsmoa_BE.trade.Entity.TradePostEntity;
import com.goodsmoa.goodsmoa_BE.trade.Entity.UserHiddenPost;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TradePostHiddenRepository extends JpaRepository<UserHiddenPost, Long> {
    List<UserHiddenPost> findAllByUser(UserEntity user);
    boolean existsByUserAndTradePost(UserEntity user, TradePostEntity tradePost);
}
