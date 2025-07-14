package com.goodsmoa.goodsmoa_BE.commission.repository;

import com.goodsmoa.goodsmoa_BE.commission.entity.CommissionSubscriptionEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommissionSubscriptionRepository extends JpaRepository<CommissionSubscriptionEntity, Long> {
    Page<CommissionSubscriptionEntity> findByUserId(UserEntity user, Pageable pageable);

    Page<CommissionSubscriptionEntity> findByCommissionId_User(UserEntity writer, Pageable pageable);
}
