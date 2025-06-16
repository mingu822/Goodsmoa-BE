package com.goodsmoa.goodsmoa_BE.demand.repository;

import com.goodsmoa.goodsmoa_BE.category.Entity.Category;
import com.goodsmoa.goodsmoa_BE.demand.entity.DemandOrderEntity;
import com.goodsmoa.goodsmoa_BE.demand.entity.DemandPostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DemandOrderRepository extends JpaRepository<DemandOrderEntity, Long> {

    @EntityGraph(attributePaths = {"demandOrderProducts"})
    Optional<DemandOrderEntity> findDemandOrderEntitiesById(Long id);

    Page<DemandOrderEntity> findByUserId(String userId, Pageable pageable);

    List<DemandOrderEntity> findByDemandPostEntity(DemandPostEntity demandPostEntity);
}
