package com.goodsmoa.goodsmoa_BE.trade.DTO;


import com.goodsmoa.goodsmoa_BE.trade.Entity.TradeImageEntity;
import com.goodsmoa.goodsmoa_BE.trade.Entity.TradePostEntity;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;


@Getter
public class TradeImageRequest {


    @NotBlank(message = "이미지 경로를 설정해주세요")
    private String imagePath;



    public TradeImageEntity toEntity(TradePostEntity tradePostEntity) {
        return TradeImageEntity.builder()
                .imagePath(imagePath)
                .tradePostEntity(tradePostEntity)
                .build();
    }
}
