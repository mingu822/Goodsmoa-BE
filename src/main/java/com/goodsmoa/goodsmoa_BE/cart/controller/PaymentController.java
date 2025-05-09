package com.goodsmoa.goodsmoa_BE.cart.controller;

import com.goodsmoa.goodsmoa_BE.cart.dto.OrderPHResponse;
import com.goodsmoa.goodsmoa_BE.cart.dto.OrderResponse;
import com.goodsmoa.goodsmoa_BE.cart.dto.TossPaymentRequest;
import com.goodsmoa.goodsmoa_BE.cart.service.TossPaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final TossPaymentService tossPaymentService;


    @PostMapping("/toss")
    public ResponseEntity<TossPaymentRequest> requestTossPayment(@RequestBody OrderResponse response) {
        return tossPaymentService.requestPayment(response);
    }

    @GetMapping("/success")
    public ResponseEntity<OrderPHResponse> confirmPayment(
            @RequestParam String orderId,
            @RequestParam String paymentKey,
            @RequestParam Integer amount) {
        return tossPaymentService.confirmPayment(paymentKey, orderId, amount);
    }

    @GetMapping("/fail")
    public ResponseEntity<TossPaymentRequest> failPayment(
            @RequestParam String code,
            @RequestParam String message,
            @RequestParam String orderId) {
        return tossPaymentService.failPayment(code, message, orderId);
    }

}