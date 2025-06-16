package com.goodsmoa.goodsmoa_BE.demand.repository;

import com.goodsmoa.goodsmoa_BE.category.Entity.Category;
import com.goodsmoa.goodsmoa_BE.demand.entity.DemandPostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DemandPostRepository extends JpaRepository<DemandPostEntity, Long> {

    // 수요조사글 ID로 상품글(+연관된 상품리스트) 찾아오기
    @EntityGraph(attributePaths = {"products"})
    Optional<DemandPostEntity> findDemandPostEntityById(Long id);

    Page<DemandPostEntity> findByUserId(String userId, Pageable pageable);

    Page<DemandPostEntity> findByUserIdAndCategory(String id, Category category, Pageable pageable);
}
