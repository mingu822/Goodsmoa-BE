package com.goodsmoa.goodsmoa_BE.demand.Db;

import com.goodsmoa.goodsmoa_BE.demand.Entity.DemandReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DemandReportRepository extends JpaRepository<DemandReportEntity, Long> {

    // 신고하기, Id로 신고글 찾기, 일정 신고횟수 누적되면 숨기기?

    // 수정, 삭제 X

}
