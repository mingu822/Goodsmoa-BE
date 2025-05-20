package com.goodsmoa.goodsmoa_BE.demand.repository;

import com.goodsmoa.goodsmoa_BE.demand.entity.DemandPostEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DemandPostRepository extends JpaRepository<DemandPostEntity, Long> {

    // 수요조사글 ID로 상품글(+연관된 상품리스트) 찾아오기
    @EntityGraph(attributePaths = {"products"})
    Optional<DemandPostEntity> findDemandPostEntityById(Long id);

    // 아직 종료되지 않았으며 state(상태)가 1인 경우(공개)를 가져온다
    List<DemandPostEntity> findAllByEndTimeAfterAndState(LocalDateTime endTime, boolean state);

    //아래의 조건에 해당하는 것을 가져온다.
    // 1. 아직 종료되지 않음
    // 2. state(상태)가 1인 경우(공개)
    // 3. 삭제되지 않은 것
    List<DemandPostEntity> findAllByEndTimeAfterAndStateAndDeletedAtIsNull(LocalDateTime endTime, boolean state);


    List<DemandPostEntity> findAllByDeletedAtBefore(LocalDateTime localDateTime);
}
