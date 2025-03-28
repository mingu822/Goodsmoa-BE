package com.goodsmoa.goodsmoa_BE.trade.Converter;

import com.goodsmoa.goodsmoa_BE.trade.DTO.Image.TradeImageRequest;
import com.goodsmoa.goodsmoa_BE.trade.DTO.Image.TradeImageResponse;
import com.goodsmoa.goodsmoa_BE.trade.DTO.Post.TradePostRequest;
import com.goodsmoa.goodsmoa_BE.trade.Entity.TradeImageEntity;
import com.goodsmoa.goodsmoa_BE.trade.Entity.TradePostEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TradeImageConverter {

    public TradeImageResponse toResponse(TradeImageEntity entities) {
         return TradeImageResponse.builder()
                 .id(entities.getId())
                 .imagePath(entities.getImagePath())
                 .build();
    }

    public TradeImageEntity toEntity(TradeImageRequest request, TradePostEntity tradePostEntity) {
        return TradeImageEntity.builder()
                .imagePath(request.getImagePath())
                .tradePostEntity(tradePostEntity)
                .build();

    }

    // 여러 이미지를 응답 형식으로 변환
    public List<TradeImageResponse> toResponseList(List<TradeImageEntity> entities) {
        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    // 여러 이미지를 엔티티 형식으로 변환
    public List<TradeImageEntity> toEntityList(TradePostRequest request, TradePostEntity tradePostEntity) {
        return request.getImagePath().stream()
                .map(imagePath -> TradeImageEntity.builder()
                        .imagePath(imagePath)
                        .tradePostEntity(tradePostEntity)
                        .build())
                .collect(Collectors.toList());
    }

}

