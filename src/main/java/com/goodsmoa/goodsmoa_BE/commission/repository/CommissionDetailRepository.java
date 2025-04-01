package com.goodsmoa.goodsmoa_BE.commission.repository;

import com.goodsmoa.goodsmoa_BE.commission.entity.CommissionDetailEntity;
import com.goodsmoa.goodsmoa_BE.commission.entity.CommissionPostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommissionDetailRepository extends JpaRepository<CommissionDetailEntity,Long> {

    List<CommissionDetailEntity> findByCommissionPostEntity(CommissionPostEntity postEntity);

}
