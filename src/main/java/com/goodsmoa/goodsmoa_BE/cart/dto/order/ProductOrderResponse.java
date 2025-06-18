package com.goodsmoa.goodsmoa_BE.cart.dto.order;

import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductOrderResponse {

    // 주문 상태
    private String status;

    // 게시글 정보
    private Long postId;
    private String title;
    private String thumbnailImage;
    private String categoryName;

    // 주문자 정보
    private String recipientName;   // 주문자명
    private String phoneNumber;     // 주문자 연락처

    // 상품 정보
    private String orderName;       // 대표 상품명
    private List<ProductDetail> products; // 개별 상품 수량, 가격

    private int productsPrice;      // 총 상품 금액
    private int deliveryPrice;      // 배송비
    private int totalPrice;         // 최종 결제 금액

    // 배송 정보
    private String deliveryName;        // 배송 방법 ID
    private String mainAddress;     // 주소
    private String postMemo;        // 배송 메모

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductDetail {
        private String name;
        private String image;
        private int quantity;
        private int price;
    }
}
