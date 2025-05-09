package com.goodsmoa.goodsmoa_BE.cart.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.goodsmoa.goodsmoa_BE.cart.config.TossProperties;
import com.goodsmoa.goodsmoa_BE.cart.converter.OrderConverter;
import com.goodsmoa.goodsmoa_BE.cart.converter.TossConverter;
import com.goodsmoa.goodsmoa_BE.cart.dto.OrderPHResponse;
import com.goodsmoa.goodsmoa_BE.cart.dto.OrderResponse;
import com.goodsmoa.goodsmoa_BE.cart.dto.TossPaymentRequest;
import com.goodsmoa.goodsmoa_BE.cart.dto.TossSuccessResponse;
import com.goodsmoa.goodsmoa_BE.cart.entity.OrderEntity;
import com.goodsmoa.goodsmoa_BE.cart.entity.PaymentEntity;
import com.goodsmoa.goodsmoa_BE.cart.repository.OrderRepository;
import com.goodsmoa.goodsmoa_BE.cart.repository.PaymentRepository;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TossPaymentService {

    private final TossProperties tossProperties;
    private final TossConverter tossConverter;

    private final OrderRepository orderRepository;
    private final OrderConverter orderConverter;

    private final PaymentRepository paymentRepository;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;


    // 결제할 때 필요한 정보를 반환
    public ResponseEntity<TossPaymentRequest> requestPayment(OrderResponse response) {
        OrderEntity orderEntity = orderRepository.findById(response.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문서 입니다."));
        TossPaymentRequest request = tossConverter.toTossPaymentRequest(orderEntity);
        return ResponseEntity.ok(request);
    }

    @Transactional
    public ResponseEntity<OrderPHResponse> confirmPayment(String paymentKey, String orderId, int amount) {

        // 결제 승인을 위한 url
        String secretKey = tossProperties.getSecretKey();
        String url = "https://api.tosspayments.com/v1/payments/confirm";
        String encodedAuth = Base64.getEncoder().encodeToString((secretKey + ":").getBytes());

        // HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON_UTF8)); // 중요
        headers.set("Authorization", "Basic " + encodedAuth);

        // HTTP 바디 설정
        Map<String, Object> body = new HashMap<>();
        body.put("paymentKey", paymentKey);
        body.put("orderId", orderId);
        body.put("amount", amount);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        // Toss 서버에 POST 요청
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        // JSON 응답을 TossSuccessResponse로 파싱
        TossSuccessResponse tossResponse;
        try {
            tossResponse = objectMapper.readValue(response.getBody(), TossSuccessResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("결제 응답 파싱 실패", e);
        }

        // 주문 정보 조회
        OrderEntity order = orderRepository.findByOrderCode(orderId)
                .orElseThrow(() -> new IllegalArgumentException("해당 주문이 존재하지 않습니다: " + orderId));

        PaymentEntity payment = tossConverter.toSucessPaymentEntity(paymentKey, orderId, amount, tossResponse, order);
        paymentRepository.save(payment);

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