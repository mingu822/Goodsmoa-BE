package com.goodsmoa.goodsmoa_BE.cart.dto.delivery;

import com.goodsmoa.goodsmoa_BE.cart.dto.order.OrderItemResponse;
import com.goodsmoa.goodsmoa_BE.cart.dto.order.OrderRequest;
import com.goodsmoa.goodsmoa_BE.cart.entity.OrderItemEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class DeliveryResponse {

    // 수령인 이름
    private String recipientName;

    // 수령인 전화번호
    private String phoneNumber;

    // 주소
    private String mainAddress;

    // 배송 메모
    private String postMemo;

    private List<OrderItemResponse> products;

    private Integer totalCount;
}
