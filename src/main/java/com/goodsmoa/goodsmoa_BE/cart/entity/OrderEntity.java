package com.goodsmoa.goodsmoa_BE.cart.entity;

import com.goodsmoa.goodsmoa_BE.product.entity.ProductDeliveryEntity;
import com.goodsmoa.goodsmoa_BE.product.entity.ProductPostEntity;
import com.goodsmoa.goodsmoa_BE.trade.Entity.TradePostEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders") // "order"는 예약어라서 plural 형태 추천
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id", nullable = false, updatable = false)
    private Long id;

    // 구매자 정보
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    // 구매자가 구매하는 상품글
    @ManyToOne
    @JoinColumn(name = "post_id")
    private ProductPostEntity productPost;

    // 판매글에서 구매자가 선택한 배달 옵션
    @ManyToOne
    @JoinColumn(name = "delivery_id")
    private ProductDeliveryEntity productDelivery;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trade_post_id")
    private TradePostEntity tradePost;

    // 주문 상태
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    // 판매자가 입력한 택배사 이름
    @Column(name = "delivery_name", length = 30)
    private String deliveryName;

    // 송장 번호
    @Column(name = "tracking_number")
    private Integer trackingNumber;

    // 구매 확정 일자
    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    // ✅ 배송 정보: 주문 시점 기준으로 복사된 값
    @Column(name = "recipient_name", length = 15)
    private String recipientName;

    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    @Column(name = "zip_code")
    private String zipCode;

    @Column(name = "main_address", length = 50, nullable = false)
    private String mainAddress;

    @Column(name = "detailed_address", length = 100)
    private String detailedAddress;

    @Column(name = "post_memo", length = 100)
    private String postMemo;

    @Column(name = "order_code",unique = true)
    private String orderCode;

    @Column(name = "order_name")
    private String orderName;

    @Column(name = "products_price")
    private Integer productsPrice;

    @Column(name = "delivery_price")
    private Integer deliveryPrice;

    @Column(name = "total_price")
    private Integer totalPrice;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemEntity> orderItems = new ArrayList<>();

    public enum OrderStatus {
        배송준비, 배송중, 배송완료
    }

}
