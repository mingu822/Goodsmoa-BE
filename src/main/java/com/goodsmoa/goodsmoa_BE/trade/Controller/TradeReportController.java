package com.goodsmoa.goodsmoa_BE.trade.Controller;

import com.goodsmoa.goodsmoa_BE.trade.DTO.Report.TradeReportRequest;
import com.goodsmoa.goodsmoa_BE.trade.DTO.Report.TradeReportResponse;
import com.goodsmoa.goodsmoa_BE.trade.Service.TradeReportService;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trade-report")
@RequiredArgsConstructor
public class TradeReportController {

    private final TradeReportService tradeReportService;

    @PostMapping("/{tradeId}")
    public ResponseEntity<TradeReportResponse> reportTrade(
            @PathVariable Long tradeId,
            @AuthenticationPrincipal UserEntity user,
            @RequestBody TradeReportRequest request
    ) {

        return tradeReportService.createReport(tradeId, user, request);
    }

    @PutMapping("/update/{reportId}")
    public ResponseEntity<TradeReportResponse> updateReport(
            @RequestBody TradeReportRequest request,
            @PathVariable Long reportId,
            @AuthenticationPrincipal UserEntity user
    ){
        return tradeReportService.updateReport(reportId, user, request);
    }

    @DeleteMapping("/delete/{reportId}")
    public ResponseEntity<String> deleteReport(
            @AuthenticationPrincipal UserEntity user,
            @PathVariable Long reportId
    ){
        return tradeReportService.deleteReport(reportId, user);
    }

    @GetMapping("/reports")
    public ResponseEntity<Page<TradeReportResponse>> getReports(
            @AuthenticationPrincipal UserEntity user,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ){
        return tradeReportService.getReport(user,pageable);
    }
}
