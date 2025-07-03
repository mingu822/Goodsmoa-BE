package com.goodsmoa.goodsmoa_BE.commission.repository;

import com.goodsmoa.goodsmoa_BE.commission.entity.CommissionPostEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommissionRepository extends JpaRepository<CommissionPostEntity,Long> {
    Page<CommissionPostEntity> findAllByUser(UserEntity user, Pageable pageable);
}
