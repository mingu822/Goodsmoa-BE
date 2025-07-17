package com.goodsmoa.goodsmoa_BE.commission.dto.apply;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseContentDto {

    private Long id;
    private String content;
}
