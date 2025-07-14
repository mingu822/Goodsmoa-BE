package com.goodsmoa.goodsmoa_BE.commission.dto.apply;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionListResponse {

    private Long id;    // 신청받은 아이디

    private Long commissionId;

    private String title;

    private String requestStatus;

    private String categoryName;

    private String sellerName;   // 판매자 이름

    private String sellerId;     // 판매자 아이디

    private String thumbnailImage;

    private LocalDateTime createdAt;
}
