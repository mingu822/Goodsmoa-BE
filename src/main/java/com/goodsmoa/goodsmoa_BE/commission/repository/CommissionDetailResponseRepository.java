package com.goodsmoa.goodsmoa_BE.commission.repository;

import com.goodsmoa.goodsmoa_BE.commission.entity.CommissionDetailEntity;
import com.goodsmoa.goodsmoa_BE.commission.entity.CommissionDetailResponseEntity;
import com.goodsmoa.goodsmoa_BE.commission.entity.CommissionSubscriptionEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommissionDetailResponseRepository extends JpaRepository<CommissionDetailResponseEntity, Long> {
    List<CommissionDetailResponseEntity> findByUserAndCommissionDetailEntityIn(UserEntity user, List<CommissionDetailEntity> details);
}
