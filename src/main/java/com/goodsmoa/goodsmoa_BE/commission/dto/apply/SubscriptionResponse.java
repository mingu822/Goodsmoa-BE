package com.goodsmoa.goodsmoa_BE.commission.dto.apply;

import com.goodsmoa.goodsmoa_BE.commission.dto.detail.CommissionDetailResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionResponse {

    private String title;

    private String categoryName;

    private String thumbnailImage;

    private Integer minimumPrice;

    private Integer maximumPrice;

    private List<CommissionDetailResponse> commissionDetail;

    private List<String> resContent;

}
