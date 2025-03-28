package com.goodsmoa.goodsmoa_BE.trade.DTO.Image;


import com.goodsmoa.goodsmoa_BE.trade.Entity.TradeImageEntity;
import com.goodsmoa.goodsmoa_BE.trade.Entity.TradePostEntity;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

import java.util.List;


@Getter
@Builder
public class TradeImageRequest {

    private long postId;
    @NotBlank(message = "이미지 경로를 설정해주세요")
    private String imagePath;
}

