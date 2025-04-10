package com.goodsmoa.goodsmoa_BE.demand.repository;

import com.goodsmoa.goodsmoa_BE.demand.entity.DemandReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DemandReportRepository extends JpaRepository<DemandReportEntity, Long> {

    // TODO 신고하기, Id로 신고글 찾기, 일정 신고횟수 누적되면 숨기기?
    // TODO 수정, 삭제 X 아니면 관리자만 가능하게?

}
