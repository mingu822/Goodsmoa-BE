package com.goodsmoa.goodsmoa_BE.trade.Converter;

import com.goodsmoa.goodsmoa_BE.trade.DTO.Report.TradeReportRequest;
import com.goodsmoa.goodsmoa_BE.trade.DTO.Report.TradeReportResponse;
import com.goodsmoa.goodsmoa_BE.trade.Entity.TradePostEntity;
import com.goodsmoa.goodsmoa_BE.trade.Entity.TradeReportEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class TradeReportConverter {

    // ✅ Request → Entity 변환
    public TradeReportEntity toEntity(TradeReportRequest request, TradePostEntity tradeEntity, UserEntity userEntity) {
        return TradeReportEntity.builder()
                .trade(tradeEntity)
                .user(userEntity)
                .content(request.getContent())
                .title(request.getTitle())
                .build();
    }

    // ✅ Entity → Response 변환 (등록)
    public TradeReportResponse toResponse(TradeReportEntity entity) {
        return TradeReportResponse.builder()
                .id(entity.getId()) // 신고 번호
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .nickName(entity.getUser() != null ? entity.getUser().getNickname() : "알 수 없음")
                .title(entity.getTitle())
                .content(entity.getContent())
                .createdAt(entity.getCreatedAt())
                .tradePostTitle(entity.getTrade() != null ? entity.getTrade().getTitle() : "제목 없음")
                .imageUrl(entity.getTrade() != null ? entity.getTrade().getThumbnailImage() : null)
                .status("대기 중")
                .build();
    }

    // ✅ Entity → Response 변환 (수정 후 리턴)
    public TradeReportResponse updateResponse(TradeReportEntity entity) {
        return TradeReportResponse.builder()
                .id(entity.getId())
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .nickName(entity.getUser() != null ? entity.getUser().getNickname() : "알 수 없음")
                .title(entity.getTitle())
                .content(entity.getContent())
                .createdAt(entity.getCreatedAt())
                .tradePostTitle(entity.getTrade() != null ? entity.getTrade().getTitle() : "제목 없음")
                .imageUrl(entity.getTrade() != null ? entity.getTrade().getThumbnailImage() : null)
                .status("대기 중")
                .build();
    }
}
