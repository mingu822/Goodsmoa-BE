package com.goodsmoa.goodsmoa_BE.commission.repository;

import com.goodsmoa.goodsmoa_BE.commission.entity.CommissionPostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommissionRepository extends JpaRepository<CommissionPostEntity,Long> {
}
