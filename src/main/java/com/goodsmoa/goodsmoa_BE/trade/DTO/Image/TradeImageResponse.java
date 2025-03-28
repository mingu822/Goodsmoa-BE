package com.goodsmoa.goodsmoa_BE.trade.DTO.Image;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TradeImageResponse {

    private Long id;

    private String imagePath;

}

