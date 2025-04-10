package com.goodsmoa.goodsmoa_BE.demand.repository;

import com.goodsmoa.goodsmoa_BE.demand.entity.DemandOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface DemandOrderRepository extends JpaRepository<DemandOrderEntity, Long> {

    // 특정 유저가 참여한 모든 수요조사 리스트
    List<DemandOrderEntity> findDemandOrderEntityByUserId(String id);
}
