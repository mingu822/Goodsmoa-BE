package com.goodsmoa.goodsmoa_BE.trade.DTO.Post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TradeSearchRequest {
    Integer categoryId;
    int page;
    int pageSize;
}
