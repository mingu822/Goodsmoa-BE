package com.goodsmoa.goodsmoa_BE.cart.service;

import com.goodsmoa.goodsmoa_BE.cart.converter.OrderConverter;
import com.goodsmoa.goodsmoa_BE.cart.converter.TossConverter;
import com.goodsmoa.goodsmoa_BE.cart.dto.OrderRequest;
import com.goodsmoa.goodsmoa_BE.cart.dto.OrderResponse;
import com.goodsmoa.goodsmoa_BE.cart.entity.OrderEntity;
import com.goodsmoa.goodsmoa_BE.cart.entity.OrderItemEntity;
import com.goodsmoa.goodsmoa_BE.cart.repository.OrderItemRepository;
import com.goodsmoa.goodsmoa_BE.cart.repository.OrderRepository;
import com.goodsmoa.goodsmoa_BE.product.entity.ProductDeliveryEntity;
import com.goodsmoa.goodsmoa_BE.product.entity.ProductEntity;
import com.goodsmoa.goodsmoa_BE.product.entity.ProductPostEntity;
import com.goodsmoa.goodsmoa_BE.product.repository.ProductDeliveryRepository;
import com.goodsmoa.goodsmoa_BE.product.repository.ProductPostRepository;
import com.goodsmoa.goodsmoa_BE.product.repository.ProductRepository;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderConverter orderConverter;

    private final ProductPostRepository productPostRepository;
    private final ProductRepository productRepository;
    private final ProductDeliveryRepository productDeliveryRepository;

    private final TossConverter tossConverter;

    // TODO 주문을 하면 결제를 하지 않아도 상품의 갯수가 줄어 들어야 하는가?
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
}