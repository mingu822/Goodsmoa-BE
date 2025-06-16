package com.goodsmoa.goodsmoa_BE.cart.dto.order;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class PurchaseHistoryResponse {

    private Long orderId;
    private String orderCode;

    // 주문자 정보
    private String recipientName;

    // 판매 유형 / 카테고리
    private String saleLabel;

    private String category;

    // 주문 상태
    private String status;

    // 결제 정보
    private String orderName;         // 예: "에코백 외 2개"
    private Integer totalQuantity;
    private Integer totalPrice;       // PaymentEntity.amount
    private LocalDateTime paymentDate;

    // 상품 목록
    private List<ProductDto> products;

    @Getter
    @Builder
    public static class ProductDto {
        private String name;
        private String imageUrl;
        private Integer price;
        private Integer quantity;
    }
}
