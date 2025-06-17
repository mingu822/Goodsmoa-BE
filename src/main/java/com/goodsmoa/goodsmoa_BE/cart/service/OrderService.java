package com.goodsmoa.goodsmoa_BE.cart.service;

import com.goodsmoa.goodsmoa_BE.cart.converter.DeliveryConverter;
import com.goodsmoa.goodsmoa_BE.cart.converter.OrderConverter;
import com.goodsmoa.goodsmoa_BE.cart.dto.delivery.DeliveryRequest;
import com.goodsmoa.goodsmoa_BE.cart.dto.delivery.DeliveryResponse;
import com.goodsmoa.goodsmoa_BE.cart.dto.delivery.TrackingResponse;
import com.goodsmoa.goodsmoa_BE.cart.dto.order.*;
import com.goodsmoa.goodsmoa_BE.cart.entity.OrderEntity;
import com.goodsmoa.goodsmoa_BE.cart.entity.OrderItemEntity;
import com.goodsmoa.goodsmoa_BE.cart.entity.PaymentEntity;
import com.goodsmoa.goodsmoa_BE.cart.repository.OrderItemRepository;
import com.goodsmoa.goodsmoa_BE.cart.repository.OrderRepository;
import com.goodsmoa.goodsmoa_BE.cart.repository.PaymentRepository;
import com.goodsmoa.goodsmoa_BE.product.entity.ProductDeliveryEntity;
import com.goodsmoa.goodsmoa_BE.product.entity.ProductEntity;
import com.goodsmoa.goodsmoa_BE.product.entity.ProductPostEntity;
import com.goodsmoa.goodsmoa_BE.product.repository.ProductDeliveryRepository;
import com.goodsmoa.goodsmoa_BE.product.repository.ProductPostRepository;
import com.goodsmoa.goodsmoa_BE.product.repository.ProductRepository;
import com.goodsmoa.goodsmoa_BE.trade.Entity.TradePostEntity;
import com.goodsmoa.goodsmoa_BE.trade.Repository.TradePostRepository;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderConverter orderConverter;

    private final ProductPostRepository productPostRepository;
    private final ProductRepository productRepository;
    private final ProductDeliveryRepository productDeliveryRepository;

    private final DeliveryConverter deliveryConverter;

    private final TrackingService trackingService;

    private final TradePostRepository tradePostRepository;

    private final PaymentRepository paymentRepository;

    // 주문서 생성
    @Transactional
    public ResponseEntity<OrderResponse> createOrder(OrderRequest request, UserEntity user) {
        // 판매글 확인
        ProductPostEntity post = productPostRepository.findById(request.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품 게시글입니다."));

        // 배송 옵션 확인
        ProductDeliveryEntity delivery = productDeliveryRepository.findById(request.getDeliveryId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 배송 옵션입니다."));

        // OrderEntity 생성 및 저장
        OrderEntity order = orderConverter.toOrderEntity(request, user, post, delivery);
        orderRepository.save(order);

        // OrderItemEntity 생성
        List<OrderItemEntity> orderItems = request.getProducts().stream()
                .map(products -> {
                    ProductEntity product = productRepository.findById(products.getProductId())
                            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));
                    return orderConverter.toOrderItemEntity(order, product, products);
                })
                .collect(Collectors.toList());

        // OrderItem 저장
        orderItemRepository.saveAll(orderItems);

        // 💡 총 상품 금액 계산
        int productsPrice = orderItems.stream()
                .mapToInt(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();

        // 💡 총 주문 금액 = 상품 금액 + 배송비
        int totalPrice = productsPrice + delivery.getPrice();

        String orderName;
        if (orderItems.size() == 1) {
            orderName = orderItems.get(0).getProduct().getName();
        } else {
            orderName = orderItems.get(0).getProduct().getName() + " 외 " + (orderItems.size() - 1) + "건";
        }
        // 💡 order에 가격 정보 설정
        order.setProductsPrice(productsPrice);
        order.setTotalPrice(totalPrice);
        order.setDeliveryPrice(delivery.getPrice());
        order.setOrderName(orderName);

        // 응답 객체 생성
        // 피그마 참조해서 필요한거 보냄
        OrderResponse orderResponse = orderConverter.toOrderResponse(order);

        return ResponseEntity.ok(orderResponse);
    }

    // 판매자가 송장번호 등록시키는 메서드
    public ResponseEntity<DeliveryResponse> createDelivery(DeliveryRequest request, UserEntity user) {

        OrderEntity order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문서입니다."));

        // 판매자 본인 확인 (일반 상품과 중고거래 상품 둘 다 고려)
        boolean isSeller = (order.getProductPost() != null && order.getProductPost().getUser().getId().equals(user.getId())) ||
                (order.getTradePost() != null && order.getTradePost().getUser().getId().equals(user.getId()));

        if(!order.getProductPost().getUser().getId().equals(user.getId())) {
            return ResponseEntity.notFound().build();
        }
        if (!isSeller) {
            return ResponseEntity.status(403).body(null); // 권한 없음
        }

        order.setDeliveryName(request.getDeliveryName());
        order.setTrackingNumber(request.getTrackingNumber());
        orderRepository.save(order);

        List<OrderItemEntity> orderItems = orderItemRepository.findByOrder(order);

        DeliveryResponse response = deliveryConverter.toResponse(order, orderItems);

        return ResponseEntity.ok(response);
    }

    // 구매자가 송장번호를 통해 정보를 확인할 때
    public TrackingResponse trackDelivery(String companyCode, String invoiceNumber) throws Exception {
        return trackingService.trackDelivery(companyCode, invoiceNumber);
    }
    // ✅ [신규 기능] 중고거래 상품 주문서 생성
    @Transactional
    public TradeOrderResponse createOrderFromTrade(TradeOrderRequest request, UserEntity buyer) {
        TradePostEntity tradePost = tradePostRepository.findById(request.getTradePostId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 중고거래 게시글입니다."));

        if (Boolean.FALSE.equals(tradePost.getDelivery())) {
            throw new IllegalStateException("택배 거래가 불가능한 상품입니다.");
        }
        if (tradePost.getTradeStatus() != TradePostEntity.TradeStatus.판매중) {
            throw new IllegalStateException("이미 판매가 진행중이거나 완료된 상품입니다.");
        }

        OrderEntity order = orderConverter.toOrderEntityFromTrade(request, buyer, tradePost);
        log.info("### DB 저장 직전 order 객체의 productDelivery 필드 값: [{}]", order.getProductDelivery());
        orderRepository.save(order);

        tradePost.setTradeStatus(TradePostEntity.TradeStatus.거래중);

        return orderConverter.toTradeOrderResponse(order);
    }

    public ResponseEntity<Page<PurchaseHistoryResponse>> getList(UserEntity user, Pageable pageable) {
        Page<PaymentEntity> paymentPage = paymentRepository.findByUserAndStatus(user, PaymentEntity.PaymentStatus.SUCCESS, pageable);

        Page<PurchaseHistoryResponse> responses = paymentPage.map(payment ->
                orderConverter.mapToDto(payment.getOrder(), payment)
        );

        return ResponseEntity.ok(responses);
    }
    // ✅ [신규 기능] 구매 내역 상세 조회
    public PurchaseHistoryResponse getPurchaseDetails(Long orderId) {
        // 1. Repository에서 쿼리 한 방으로 주문과 관련 정보를 모두 가져온다.
        //    (OrderRepository에 findByIdWithDetails 메서드 추가 필요!)
        OrderEntity order = orderRepository.findByIdWithDetails(orderId)
                .orElseThrow(() -> new EntityNotFoundException("주문 내역을 찾을 수 없습니다. ID: " + orderId));

        // 3. 주문에 연결된 '성공(SUCCESS)' 상태의 결제 정보를 찾는다.
        PaymentEntity payment = paymentRepository.findByOrderAndStatus(order, PaymentEntity.PaymentStatus.SUCCESS)
                .orElseThrow(() -> new EntityNotFoundException("성공한 결제 정보를 찾을 수 없습니다. Order ID: " + orderId));

        // 4. 컨버터를 사용해 DTO로 변환하여 반환한다.
        return orderConverter.mapToDto(order, payment);
    }
}