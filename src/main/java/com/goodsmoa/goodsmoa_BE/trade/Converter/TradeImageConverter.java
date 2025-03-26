package com.goodsmoa.goodsmoa_BE.trade.Converter;

import com.goodsmoa.goodsmoa_BE.trade.DTO.Image.TradeImageResponse;
import com.goodsmoa.goodsmoa_BE.trade.Entity.TradeImageEntity;
import org.springframework.stereotype.Component;

@Component
public class TradeImageConverter {


    public TradeImageResponse toResponse(TradeImageEntity entity) {
        return TradeImageResponse.builder()
                .imagePath(entity.getImagePath())
                .id(entity.getId())
                .imageUrl(entity.getImagePath()) // 실제 이미지 URL 생성
                .build();
    }
}

