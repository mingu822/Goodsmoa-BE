package com.goodsmoa.goodsmoa_BE.cart.dto.order;

import com.goodsmoa.goodsmoa_BE.cart.entity.OrderEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderPHResponse {

    // 결제 완료 후 보내주는 정보

    private Long id;

    // 상품글 이름
    private String postName;

    private LocalDateTime paidAt;

    private String checkLabel;

    // 상품글 대표 이미지
    private String postThumbnail;
    
    // 주문자명
    private String userName;

    // 주문자 연락처
    private String userPhone;

    // 현재 배송 상태
    private OrderEntity.OrderStatus status;

    // 택배사
    private String deliveryName;

    // 송장번호
    private Integer postNumber;

    // 확인시간
    private LocalDateTime confirmedAt;

    // 받는사람명
    private String recipientName;

    // 받는사람 연락처
    private String phoneNumber;

    // 주소
    private String mainAddress;

    // 배송메모
    private String postMemo;

    // 상품가격
    private Integer productsPrice;

    // 배송비
    private Integer deliveryPrice;

    // 최종 결제 금액
    private Integer totalPrice;

    private List<OrderItemResponse> products;

}
