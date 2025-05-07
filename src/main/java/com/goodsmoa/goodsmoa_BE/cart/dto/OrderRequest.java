package com.goodsmoa.goodsmoa_BE.cart.dto;

import lombok.*;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {

    // 구매자 아이디
    private String userId;

    // 판매글 아이디
    private Long postId;

    // 배달방식 아이디
    private Long deliveryId;

    // 수령인 이름
    private String recipientName;

    // 수령인 전화번호
    private String phoneNumber;

    private String zipCode;

    // 주소
    private String mainAddress;

    // 상세 주소
    private String detailedAddress;

    // 배송 메모
    private String postMemo;

    private List<Products> products;

    @Data
    public static class Products {
        private Long productId;
        private Integer quantity;
    }
}