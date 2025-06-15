package com.goodsmoa.goodsmoa_BE.trade.Repository;

import com.goodsmoa.goodsmoa_BE.category.Entity.Category;
import com.goodsmoa.goodsmoa_BE.trade.Entity.TradePostEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface TradePostRepository extends JpaRepository<TradePostEntity, Long> {
    Page<TradePostEntity> findByIdNotIn(List<Long> ids, Pageable pageable);

    Page<TradePostEntity> findByUser(UserEntity user, Pageable pageable);

    // 카테고리별로 내가 쓴 글 조회
    Page<TradePostEntity> findByUserAndCategory(UserEntity user, Category category, Pageable pageable);

    // 숨김 글 제외하고, 카테고리 없이 내가 쓴 글 전체 조회
    Page<TradePostEntity> findByUserAndIdNotIn(UserEntity user, List<Long> hiddenPostIds, Pageable pageable);

    // 숨김 글 제외하고, 카테고리별로 내가 쓴 글 조회
    Page<TradePostEntity> findByUserAndCategoryAndIdNotIn(UserEntity user, Category category, List<Long> hiddenPostIds, Pageable pageable);
}
