package com.goodsmoa.goodsmoa_BE.commission.dto.apply;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionUpdateRequest {

    private Long id;            // res Id

    private Long commissionId;

    private Long detailId;

    private String resContent;
}
