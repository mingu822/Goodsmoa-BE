package com.goodsmoa.goodsmoa_BE.demand.Db;

import com.goodsmoa.goodsmoa_BE.demand.Entity.DemandEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DemandEntityRepository extends JpaRepository<DemandEntity, Long> {

    // 수요조사글 ID로 상품글(+연관된 상품리스트) 찾아오기
    @EntityGraph(attributePaths = {"products"})
    Optional<DemandEntity> findDemandEntityById(Long id);

    // 아직 종료되지 않았으며 state(상태)가 1인 경우(공개)를 가져온다
    List<DemandEntity> findAllByEndTimeAfterAndState(LocalDateTime endTime, int state);
}
