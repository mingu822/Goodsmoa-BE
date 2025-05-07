package com.goodsmoa.goodsmoa_BE.cart.controller;

import com.goodsmoa.goodsmoa_BE.cart.dto.OrderRequest;
import com.goodsmoa.goodsmoa_BE.cart.dto.OrderResponse;
import com.goodsmoa.goodsmoa_BE.cart.service.OrderService;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 결제 생성
    @PostMapping("/create")
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest request, @AuthenticationPrincipal UserEntity user) {
        return orderService.createOrder(request, user);
    }

}