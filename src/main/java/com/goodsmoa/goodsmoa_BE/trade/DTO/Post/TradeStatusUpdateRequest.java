package com.goodsmoa.goodsmoa_BE.trade.DTO.Post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TradeStatusUpdateRequest {
    private String tradeStatus;
}
