package com.goodsmoa.goodsmoa_BE.trade.Converter;

import com.goodsmoa.goodsmoa_BE.trade.DTO.Report.TradeReportRequest;
import com.goodsmoa.goodsmoa_BE.trade.DTO.Report.TradeReportResponse;
import com.goodsmoa.goodsmoa_BE.trade.Entity.TradePostEntity;
import com.goodsmoa.goodsmoa_BE.trade.Entity.TradeReportEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class TradeReportConverter {

    // Request → Entity 변환
    public TradeReportEntity toEntity(TradeReportRequest request, TradePostEntity tradeEntity, UserEntity userEntity) {
        return TradeReportEntity.builder()
                .trade(tradeEntity)
                .user(userEntity)
                .content(request.getContent())
                .title(request.getTitle())
                .build();
    }

    // Entity → Response 변환
    public TradeReportResponse toResponse(TradeReportEntity entity) {
        return TradeReportResponse.builder()
                .title(entity.getTitle())
                .content(entity.getContent())
                .build();
    }
}

