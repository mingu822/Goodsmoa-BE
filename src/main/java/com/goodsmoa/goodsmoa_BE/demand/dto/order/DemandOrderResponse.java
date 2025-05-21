package com.goodsmoa.goodsmoa_BE.demand.dto.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.goodsmoa.goodsmoa_BE.demand.entity.DemandOrderProductEntity;
import com.goodsmoa.goodsmoa_BE.demand.entity.DemandPostEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
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
    // FIXME: 유저정보 DTO 로 변경
    private UserEntity user;
    
    // 주문한 수요조사 글
    // FIXME: 수요조사 DTO 로 변경
    private DemandPostEntity postEntity;
    
    // 주문한 상품 목록
    private List<DemandOrderProductResponse> demandOrderProducts;
}
