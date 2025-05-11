package com.goodsmoa.goodsmoa_BE.trade.Service;

import com.goodsmoa.goodsmoa_BE.trade.Converter.TradeReportConverter;
import com.goodsmoa.goodsmoa_BE.trade.DTO.Report.TradeReportRequest;
import com.goodsmoa.goodsmoa_BE.trade.DTO.Report.TradeReportResponse;
import com.goodsmoa.goodsmoa_BE.trade.Entity.TradePostEntity;
import com.goodsmoa.goodsmoa_BE.trade.Entity.TradeReportEntity;
import com.goodsmoa.goodsmoa_BE.trade.Repository.TradePostRepository;
import com.goodsmoa.goodsmoa_BE.trade.Repository.TradeReportRepository;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import com.goodsmoa.goodsmoa_BE.user.Repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TradeReportService {

    private final TradeReportRepository tradeReportRepository;
    private final TradePostRepository tradePostRepository;
    private final UserRepository userRepository;
    private final TradeReportConverter tradeReportConverter;

    @Transactional
    public ResponseEntity<TradeReportResponse> createReport(Long tradeId, UserEntity user, TradeReportRequest request) {
        TradePostEntity trade = tradePostRepository.findById(tradeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 거래글입니다."));
//        UserEntity user = userRepository.findById(userId)
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        TradeReportEntity report = tradeReportConverter.toEntity(request, trade, user);
        tradeReportRepository.save(report);

        return ResponseEntity.ok(tradeReportConverter.toResponse(report));
    }

    @Transactional
    public ResponseEntity<TradeReportResponse> updateReport(Long reportId, UserEntity user, TradeReportRequest request) {
        TradeReportEntity report = tradeReportRepository.findById(reportId).orElseThrow(
                ()-> new IllegalArgumentException("존재하지 않은 게시물 입니다"));
        if(!userRepository.existsById(user.getId())) {
            return ResponseEntity.notFound().build();
        }
        report.updateReport(request);

        tradeReportRepository.save(report);

        return ResponseEntity.ok(tradeReportConverter.updateResponse(report));
    }
    @Transactional
    public ResponseEntity<String> deleteReport(Long reportId, UserEntity user) {
        TradeReportEntity report = tradeReportRepository.findById(reportId).orElseThrow(
                ()-> new IllegalArgumentException("존재하지 않는 게시물"));
        if(!userRepository.existsById(user.getId())) {
            return ResponseEntity.notFound().build();
        }
        tradeReportRepository.delete(report);
        return ResponseEntity.ok("삭제 완료되었습니다.");
    }
    @Transactional
    public ResponseEntity<Page<TradeReportResponse>> getReport(UserEntity user, Pageable pageable) {
        Page<TradeReportEntity> reportedPage = tradeReportRepository.findAllByUser(user, pageable);

        Page<TradeReportResponse> responsePage = reportedPage.map(tradeReportConverter::toResponse);

        return ResponseEntity.ok(responsePage);
    }
}
