package com.goodsmoa.goodsmoa_BE.commission.dto.post;

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
public class PostDetailResponse {

    private Long id;

    private String title;

    private String categoryName;

    private String content;

    private String thumbnailImage;

    private Integer requestLimited;

    private Integer minimumPrice;

    private Integer maximumPrice;

    private String hashtag;

    private Long views;

    List<CommissionDetailResponse> commissionDetail;

}
