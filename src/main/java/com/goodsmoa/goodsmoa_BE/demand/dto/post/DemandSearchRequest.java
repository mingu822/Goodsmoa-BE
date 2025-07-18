package com.goodsmoa.goodsmoa_BE.demand.dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DemandSearchRequest{
    Integer categoryId;
    int page;
    int pageSize;
}
