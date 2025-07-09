package com.goodsmoa.goodsmoa_BE.commission.repository;

import com.goodsmoa.goodsmoa_BE.commission.entity.CommissionLikeEntity;
import com.goodsmoa.goodsmoa_BE.commission.entity.CommissionPostEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface CommissionLikeRepository extends JpaRepository<CommissionLikeEntity,Long> {
    CommissionLikeEntity findByCommissionIdAndUserId(CommissionPostEntity entity, UserEntity userId);

    boolean existsByCommissionIdAndUserId(CommissionPostEntity entity, UserEntity user);

    Page<CommissionLikeEntity> findByUserId(UserEntity user, Pageable pageable);

    @Query("SELECT l.commissionId.id FROM CommissionLikeEntity l " +
            "WHERE l.userId.id = :userId " +
            "AND l.commissionId.id IN :postIds")
    Set<Long> findLikedIdsByUserIdAndCommissionId(
            @Param("userId") String userId,
            @Param("postIds") List<Long> postIds
    );
}
