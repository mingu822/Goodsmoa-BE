package com.goodsmoa.goodsmoa_BE.trade.DTO.Image;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TradeImgUpdateRequest {

    private Long id;

    private String imagePath;
}
