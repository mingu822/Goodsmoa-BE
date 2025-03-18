package com.goodsmoa.goodsmoa_BE.demand.Db;

import com.goodsmoa.goodsmoa_BE.demand.Entity.DemandEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DemandLikeRepository extends JpaRepository<DemandEntity, Long> {


}
