package com.goodsmoa.goodsmoa_BE.demand.dto.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.goodsmoa.goodsmoa_BE.demand.dto.post.DemandPostOmittedResponse;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Builder @Getter
public class DemandOrderResponse {
    // 주문 ID
    private Long id;

    // 주문 생성일
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime creatAt;

    // 주문한 유저 정보
    private String userId;

    private String userNickName;

    private String userImage;
    
    // 주문한 수요조사 글
    private DemandPostOmittedResponse demandPostOmittedResponse;
    
    // 주문한 상품 목록
    private List<DemandOrderProductResponse> demandOrderProducts;
}
