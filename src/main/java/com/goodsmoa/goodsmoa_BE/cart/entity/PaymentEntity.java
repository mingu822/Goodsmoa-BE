package com.goodsmoa.goodsmoa_BE.cart.entity;

import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String paymentKey;    // 토스에서 받은 결제 키

    private String orderCode;       // order에서 orderCode

    private Integer amount;       // 가격

    private String method;        // 카드/가상계좌/간편결제에 대한 정보

    private String orderName;     // 어떤 상품인지에 대한 이름

    private String customerName;    // 주문자명

    private LocalDateTime paidAt;   // 결제 시간

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    private OrderEntity order;

    public enum PaymentStatus {
        READY, SUCCESS, FAILED
    }

}
