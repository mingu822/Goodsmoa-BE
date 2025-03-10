package com.goodsmoa.goodsmoa_BE.trade.DTO;


import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TradeImageRequest {

    @NotBlank(message = "이미지 경로를 설정해주세요")
    private String imagePath;

    // TradeImageEntity 객체를 DTO로 변환하는 메서드
    @Builder
    public TradeImageRequest(String imagePath) {
        this.imagePath = imagePath;

    }
}
