package com.goodsmoa.goodsmoa_BE.demand.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity @Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "demand_product_order")
public class DemandOrderProductEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 구매를 원하는 개수
    @Column(nullable = false)
    private int quantity;

    // 수요조사글 상품 id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "demand_post_product_id")
    private DemandPostProductEntity postProductEntity;

    // 수요조사 주문서 id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "demand_order_id")
    private DemandOrderEntity orderEntity;

    public void updateQuantity(int quantity) {
        this.quantity = quantity;
    }
}
