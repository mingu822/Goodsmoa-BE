package com.goodsmoa.goodsmoa_BE.commission.dto.detail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommissionDetailRequest {

    private Long commissionId;

    private String title;

    private String reqContent;
}
