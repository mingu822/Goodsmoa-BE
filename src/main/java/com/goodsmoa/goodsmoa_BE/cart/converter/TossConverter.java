package com.goodsmoa.goodsmoa_BE.cart.converter;

import com.goodsmoa.goodsmoa_BE.cart.dto.payment.TossPaymentRequest;
import com.goodsmoa.goodsmoa_BE.cart.dto.payment.TossSuccessResponse;
import com.goodsmoa.goodsmoa_BE.cart.entity.OrderEntity;
import com.goodsmoa.goodsmoa_BE.cart.entity.PaymentEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TossConverter {


    public TossPaymentRequest toTossPaymentRequest(OrderEntity entity) {
        return TossPaymentRequest.builder()
                .orderCode(entity.getOrderCode())
                .orderName(entity.getOrderName())
                .customerName(entity.getUser().getNickname())
                .amount(entity.getTotalPrice())
                .successUrl("http://goodsmoa.kro.kr:8080/payment/success")        // 성공했을 때
                .failUrl("http://goodsmoa.kro.kr:8080/payment/fail")              // 실패했을 때
                .build();
    }

    public PaymentEntity toSucessPaymentEntity(String paymentKey, String orderId, int amount, TossSuccessResponse response, OrderEntity order) {
        return   PaymentEntity.builder()
                .paymentKey(paymentKey)
                .orderCode(orderId)
                .amount(amount)
                .method(response.getMethod())
                .orderName(response.getOrderName())
                .customerName(order.getUser().getName())    // ????
                .paidAt(LocalDateTime.now()) // approvedAt = 결제 완료 시각
                .status(PaymentEntity.PaymentStatus.SUCCESS)
                .order(order)
                .user(order.getUser()) // order에 이미 user가 있으므로 활용
                .build();
    }

    public PaymentEntity toFailPaymentEntity(OrderEntity order, String orderId) {
        return   PaymentEntity.builder()
                .orderCode(orderId)
                .customerName(order.getUser().getName())
                .status(PaymentEntity.PaymentStatus.FAILED)
                .order(order)
                .user(order.getUser())
                .build();
    }
}
