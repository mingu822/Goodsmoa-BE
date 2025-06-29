package com.goodsmoa.goodsmoa_BE.trade.Repository;

import com.goodsmoa.goodsmoa_BE.trade.Entity.TradeLikeEntity;
import com.goodsmoa.goodsmoa_BE.trade.Entity.TradePostEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TradeLikeRepository extends JpaRepository<TradeLikeEntity, Long> {
    Optional<TradeLikeEntity> findByTradeAndUser(TradePostEntity trade, UserEntity user);
    boolean existsByTradeAndUser(TradePostEntity trade, UserEntity user);

//    List<TradeLikeEntity> findAllByUser(UserEntity user);

    Page<TradeLikeEntity> findAllByUser(UserEntity user, Pageable pageable);

    @Query("SELECT tl.trade.id FROM TradeLikeEntity tl WHERE tl.user.id = :userId AND tl.trade.id IN :postIds")
    List<Long> findLikedPostIdsByUserIdAndPostIdsIn(@Param("userId") String userId, @Param("postIds") List<Long> postIds);

    @Query("SELECT l.trade.id FROM TradeLikeEntity l " + // 게시글 ID 선택
            "WHERE l.user.id = :userId " +               // UserEntity의 id 필드
            "AND l.trade.id IN :postIds")                // TradePostEntity의 id 필드
    Set<Long> findLikedIdsByUserAndPosts(
            @Param("userId") String userId,
            @Param("postIds") List<Long> postIds
    );

}
