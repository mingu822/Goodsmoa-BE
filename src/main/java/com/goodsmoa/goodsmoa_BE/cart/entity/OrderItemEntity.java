package com.goodsmoa.goodsmoa_BE.cart.entity;

import com.goodsmoa.goodsmoa_BE.product.entity.ProductEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="order_item")
public class OrderItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id", nullable = false, updatable = false)
    private Long id;

    // 어떤 주문인지
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    // 어떤 상품인지
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    // 몇 개 샀는지
    @Column(nullable = false)
    private Integer quantity;

}