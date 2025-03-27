package com.goodsmoa.goodsmoa_BE.demand.repository;

import com.goodsmoa.goodsmoa_BE.demand.entity.DemandParticipateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DemandParticipateRepository extends JpaRepository<DemandParticipateEntity, Long> {

    Optional<DemandParticipateEntity> findDemandParticipateEntityById(Long id);
}
