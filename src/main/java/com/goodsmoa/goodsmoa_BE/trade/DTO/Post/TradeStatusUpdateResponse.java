package com.goodsmoa.goodsmoa_BE.trade.DTO.Post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeStatusUpdateResponse {
    private Long postId;
    private String tradeStatus;
}
