package com.goodsmoa.goodsmoa_BE.cart.controller;

import com.goodsmoa.goodsmoa_BE.cart.dto.delivery.DeliveryRequest;
import com.goodsmoa.goodsmoa_BE.cart.dto.delivery.DeliveryResponse;
import com.goodsmoa.goodsmoa_BE.cart.dto.delivery.TrackingResponse;
import com.goodsmoa.goodsmoa_BE.cart.dto.order.*;
import com.goodsmoa.goodsmoa_BE.cart.service.OrderService;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

    // 판매자가 송장번호를 등록 시킬 때
    @PostMapping("/delivery")
    public ResponseEntity<DeliveryResponse> createDelivery(@RequestBody DeliveryRequest request, @AuthenticationPrincipal UserEntity user){
        return orderService.createDelivery(request, user);
    }

    @GetMapping("/list")
    public ResponseEntity<Page<PurchaseHistoryResponse>> listOrder(
            @AuthenticationPrincipal UserEntity user,
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return orderService.getList(user, pageable);
    }

    // ✅ [신규 기능] 중고거래 상품 주문 생성
    @PostMapping("/trade/create")
    public ResponseEntity<TradeOrderResponse> createTradeOrder(
            @RequestBody TradeOrderRequest request,
            @AuthenticationPrincipal UserEntity user) {
        TradeOrderResponse  orderResponse = orderService.createOrderFromTrade(request, user);
        return ResponseEntity.ok(orderResponse);
    }
    // TODO 택배를 조회할 때 택배사 별로 코드를 줘야 하는데 어떻게 줘야 하는가?
    // 구매자가 송장번호를 통해 정보를 확인 할 때
    @GetMapping("/tracking")
    public ResponseEntity<TrackingResponse> trackDelivery(
            @RequestParam String companyCode,
            @RequestParam String invoiceNumber
    ) throws Exception {
        return ResponseEntity.ok(orderService.trackDelivery(companyCode, invoiceNumber));
    }
    @GetMapping("/{orderId}")
    public ResponseEntity<PurchaseHistoryResponse> getOrder(@PathVariable Long orderId)
             {
        PurchaseHistoryResponse response = orderService.getPurchaseDetails(orderId);
        return ResponseEntity.ok(response);
    }


}