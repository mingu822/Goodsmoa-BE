package com.goodsmoa.goodsmoa_BE.cart.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.goodsmoa.goodsmoa_BE.cart.config.TossProperties;
import com.goodsmoa.goodsmoa_BE.cart.converter.OrderConverter;
import com.goodsmoa.goodsmoa_BE.cart.converter.TossConverter;
import com.goodsmoa.goodsmoa_BE.cart.dto.order.OrderPHResponse;
import com.goodsmoa.goodsmoa_BE.cart.dto.order.OrderResponse;
import com.goodsmoa.goodsmoa_BE.cart.dto.payment.TossPaymentRequest;
import com.goodsmoa.goodsmoa_BE.cart.dto.payment.TossSuccessResponse;
import com.goodsmoa.goodsmoa_BE.cart.entity.OrderEntity;
import com.goodsmoa.goodsmoa_BE.cart.entity.OrderItemEntity;
import com.goodsmoa.goodsmoa_BE.cart.entity.PaymentEntity;
import com.goodsmoa.goodsmoa_BE.cart.repository.OrderItemRepository;
import com.goodsmoa.goodsmoa_BE.cart.repository.OrderRepository;
import com.goodsmoa.goodsmoa_BE.cart.repository.PaymentRepository;
import com.goodsmoa.goodsmoa_BE.product.entity.ProductEntity;
import com.goodsmoa.goodsmoa_BE.product.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TossPaymentService {

    private final TossProperties tossProperties;
    private final TossConverter tossConverter;

    private final OrderRepository orderRepository;
    private final OrderConverter orderConverter;

    private final PaymentRepository paymentRepository;

    private final OrderItemRepository orderItemRepository;

    private final ProductRepository productRepository;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;


    // 결제할 때 필요한 정보를 반환
    public ResponseEntity<TossPaymentRequest> requestPayment(OrderResponse response) {
        OrderEntity orderEntity = orderRepository.findById(response.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문서 입니다."));
        TossPaymentRequest request = tossConverter.toTossPaymentRequest(orderEntity);
        return ResponseEntity.ok(request);
    }

    // 결제 성공 했을 때
    @Transactional
    public ResponseEntity<OrderPHResponse> confirmPayment(String paymentKey, String orderCode, int amount) {

        // (1) 결제 승인 요청
        String secretKey = tossProperties.getSecretKey();
        String url = "https://api.tosspayments.com/v1/payments/confirm";
        String encodedAuth = Base64.getEncoder().encodeToString((secretKey + ":").getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON_UTF8));
        headers.set("Authorization", "Basic " + encodedAuth);

        Map<String, Object> body = new HashMap<>();
        body.put("paymentKey", paymentKey);
        body.put("orderId", orderCode);
        body.put("amount", amount);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        TossSuccessResponse tossResponse;
        try {
            tossResponse = objectMapper.readValue(response.getBody(), TossSuccessResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("결제 응답 파싱 실패", e);
        }

        // (2) 주문 조회
        OrderEntity order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new IllegalArgumentException("해당 주문이 존재하지 않습니다: " + orderCode));

        // (3) ✅ 상품 수량 차감 및 품절 처리
        List<OrderItemEntity> orderItems = orderItemRepository.findByOrder(order);
        for (OrderItemEntity item : orderItems) {
            ProductEntity product = item.getProduct();
            int currentQty = product.getQuantity();
            int orderQty = item.getQuantity();

            if (currentQty < orderQty) {
                throw new IllegalStateException("상품 재고가 부족합니다: " + product.getName());
            }

            // 수량 차감
            product.setQuantity(currentQty - orderQty);

            // 품절 상태 처리
            if (product.getQuantity() <= 0) {
                product.setAvailable(ProductEntity.AvailabilityStatus.품절);
            }
            productRepository.save(product);
        }

        // (4) 결제 정보 저장
        PaymentEntity payment = tossConverter.toSucessPaymentEntity(paymentKey, orderCode, amount, tossResponse, order);
        paymentRepository.save(payment);

        // (5) 응답 반환
        OrderPHResponse orderResponse = orderConverter.toOrderPHResponse(order);
        return ResponseEntity.ok(orderResponse);
    }


    @Transactional
    public ResponseEntity<TossPaymentRequest> failPayment(String code,String message, String orderId) {
        // 주문 정보 조회
        OrderEntity order = orderRepository.findByOrderCode(orderId)
                .orElseThrow(() -> new IllegalArgumentException("해당 주문이 존재하지 않습니다: " + orderId));

        PaymentEntity payment = tossConverter.toFailPaymentEntity(order, orderId);
        paymentRepository.save(payment);

        // TossPaymentRequest 다시 생성
        TossPaymentRequest tossPaymentRequest = tossConverter.toTossPaymentRequest(order);

        log.info("실패한 코드 : "+code);
        log.info("실패한 이유 : " +message);

        // JSON 응답으로 TossPaymentRequest 반환 → 프론트에서 받은 후 /test.html로 리다이렉트하면서 쿼리 파라미터나 세션에 담아 사용
        return ResponseEntity.ok(tossPaymentRequest);
    }
}