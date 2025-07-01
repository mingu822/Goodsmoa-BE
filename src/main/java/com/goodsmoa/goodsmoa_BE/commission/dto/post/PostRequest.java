package com.goodsmoa.goodsmoa_BE.commission.dto.post;

import com.goodsmoa.goodsmoa_BE.commission.dto.detail.CommissionDetailRequest;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostRequest {

    private Long id;

    private String title;

    private String content;

    private String thumbnailImage;

    private Integer requestLimited;

    private Integer minimumPrice;

    private Integer maximumPrice;

    private String hashtag;

    private Integer categoryId;

    private List<CommissionDetailRequest> details;

}
