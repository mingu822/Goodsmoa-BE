package com.goodsmoa.goodsmoa_BE.demand.repository;

import com.goodsmoa.goodsmoa_BE.demand.entity.DemandPostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DemandLikeRepository extends JpaRepository<DemandPostEntity, Long> {


}
