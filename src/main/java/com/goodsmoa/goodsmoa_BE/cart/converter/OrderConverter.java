package com.goodsmoa.goodsmoa_BE.cart.converter;

import com.goodsmoa.goodsmoa_BE.cart.dto.order.*;
import com.goodsmoa.goodsmoa_BE.cart.entity.OrderEntity;
import com.goodsmoa.goodsmoa_BE.cart.entity.OrderItemEntity;
import com.goodsmoa.goodsmoa_BE.cart.entity.PaymentEntity;
import com.goodsmoa.goodsmoa_BE.product.entity.ProductDeliveryEntity;
import com.goodsmoa.goodsmoa_BE.product.entity.ProductEntity;
import com.goodsmoa.goodsmoa_BE.product.entity.ProductPostEntity;
import com.goodsmoa.goodsmoa_BE.trade.Entity.TradePostEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import com.goodsmoa.goodsmoa_BE.cart.dto.order.PurchaseHistoryResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class OrderConverter {


    /** ✅ OrderRequest를 기반으로 OrderEntity를 생성 */
    public OrderEntity toOrderEntity(OrderRequest request, UserEntity user, ProductPostEntity post, ProductDeliveryEntity delivery) {
        return OrderEntity.builder()
                .user(user)
                .productPost(post)
                .productDelivery(delivery)
                .status(OrderEntity.OrderStatus.배송준비) // 주문 생성 시 기본값
                .recipientName(request.getRecipientName())
                .phoneNumber(request.getPhoneNumber())
                .zipCode(request.getZipCode())
                .mainAddress(request.getMainAddress())
                .detailedAddress(request.getDetailedAddress())
                .postMemo(request.getPostMemo())
                .orderCode("order-"+ UUID.randomUUID())
                .deliveryPrice(delivery.getPrice())
                .build();
    }

    /** ✅ ProductOrder를 기반으로 OrderItemEntity를 생성 */
    public OrderItemEntity toOrderItemEntity(OrderEntity order, ProductEntity product, OrderRequest.Products products) {
        return OrderItemEntity.builder()
                .order(order)
                .product(product)
                .quantity(products.getQuantity())
                .build();
    }

    /** ✅ OrderEntity를 OrderResponse로 변환 */
    public OrderResponse toOrderResponse(OrderEntity order) {
        return OrderResponse.builder()
                .orderId(order.getId())
                .postName(order.getProductPost().getTitle())
                .postThumbnail(order.getProductPost().getThumbnailImage())
                .productsPrice(order.getProductsPrice())
                .deliveryPrice(order.getDeliveryPrice())
                .totalPrice(order.getTotalPrice())
                .build();
    }

    // todo nickname인걸 전부 name으로 변경
    public OrderPHResponse toOrderPHResponse(OrderEntity order, PaymentEntity payment) {

        String postName;
        String postThumbnail;

        // 1. 일반 상품 판매 게시글인 경우
        if (order.getProductPost() != null) {
            ProductPostEntity productPost = order.getProductPost();
            postName = productPost.getTitle();
            postThumbnail = productPost.getThumbnailImage(); // ProductPost의 썸네일 사용
        }
        // 2. 중고 거래 게시글인 경우
        else if (order.getTradePost() != null) {
            TradePostEntity tradePost = order.getTradePost();
            postName = tradePost.getTitle();

            // 대표 이미지 URL 추출 (이전에 했던 로직과 동일)
            String representativeImageUrl = null;
            if (tradePost.getThumbnailImage() != null && !tradePost.getThumbnailImage().isEmpty()) {
                // TradeImageEntity에 getUrl() 메서드가 있다고 가정
                // 실제 이미지 URL을 가져오는 메서드명으로 수정해주세요.
                representativeImageUrl = tradePost.getThumbnailImage();
            }
            postThumbnail = representativeImageUrl;
        }
        // 3. 둘 다 없는 예외적인 경우
        else {
            // 데이터에 문제가 있는 상황이므로 예외를 발생시켜 빠르게 인지하도록 함
            throw new IllegalStateException("주문에 연결된 판매 게시글 정보가 없습니다. Order ID: " + order.getId());
        }

        return OrderPHResponse.builder()
                .id(order.getId())
                .userName(order.getUser().getNickname())
                .paidAt(payment.getPaidAt())
                .userPhone(order.getUser().getPhoneNumber())
                .postName(postName) // 위에서 확정된 게시글 제목
                .postThumbnail(postThumbnail) // 위에서 확정된 썸네일
                .status(order.getStatus())
                .deliveryName(order.getDeliveryName())
                .postNumber(order.getTrackingNumber())
                .confirmedAt(order.getConfirmedAt())
                .recipientName(order.getRecipientName())
                .phoneNumber(order.getPhoneNumber())
                .mainAddress(order.getMainAddress())
                .postMemo(order.getPostMemo())
                .productsPrice(order.getProductsPrice())
                .deliveryPrice(order.getDeliveryPrice())
                .totalPrice(order.getTotalPrice())
                .build();
    }
    public OrderEntity toOrderEntityFromTrade(TradeOrderRequest request, UserEntity user, TradePostEntity tradePost) {
        Integer productsPrice = tradePost.getProductPrice();
        Integer deliveryPrice = tradePost.getDeliveryPrice() != null ? tradePost.getDeliveryPrice().intValue() : 0;
        Integer totalPrice = productsPrice + deliveryPrice;

        return OrderEntity.builder()
                .user(user)
                .tradePost(tradePost) // ✅ tradePost 필드에 TradePostEntity 객체를 직접 설정
                .status(OrderEntity.OrderStatus.배송준비)
//                .recipientName(request.getRecipientName())
//                .phoneNumber(request.getPhoneNumber())
//                .zipCode(request.getZipCode())
                .mainAddress(request.getMainAddress())
                .detailedAddress(request.getDetailedAddress())
                .postMemo(request.getPostMemo())
                .orderCode("order-"+ UUID.randomUUID())
                .orderName(tradePost.getTitle())
                .productsPrice(productsPrice)
                .deliveryPrice(deliveryPrice)
                .totalPrice(totalPrice)
                .build();
    }
    public TradeOrderResponse toTradeOrderResponse(OrderEntity order) {
        // 이 메서드는 중고거래 주문(tradePost가 채워진)만 처리하므로 안전하게 getTradePost()를 사용할 수 있어.
        return TradeOrderResponse.builder()
                .orderId(order.getId())
                .orderCode(order.getOrderCode())
                .orderName(order.getOrderName())
                .mainAddress(order.getMainAddress())
                .postMemo(order.getPostMemo())
                .productsPrice(order.getProductsPrice())
                .deliveryPrice(order.getDeliveryPrice())
                .totalPrice(order.getTotalPrice())
                .postTitle(order.getTradePost().getTitle()) // TradePost에서 정보 가져오기
                .postThumbnail(order.getTradePost().getThumbnailImage()) // TradePost에서 정보 가져오기
                .sellerNickname(order.getTradePost().getUser().getNickname()) // TradePost에서 정보 가져오기
                .build();
    }

    public PurchaseHistoryResponse mapToDto(OrderEntity order, PaymentEntity payment) {

        // 공통 정보를 먼저 빌더에 설정
        var builder = PurchaseHistoryResponse.builder()
                .orderId(order.getId())
                .orderCode(order.getOrderCode())
                .recipientName(order.getRecipientName())
                .status(order.getStatus().name())
                .totalPrice(payment.getAmount())
                .paymentDate(payment.getPaidAt());

        // 1. 일반 상품 판매일 경우
        if (order.getProductPost() != null) {
            List<PurchaseHistoryResponse.ProductDto> productDtos = order.getOrderItems().stream()
                    .map(item -> PurchaseHistoryResponse.ProductDto.builder()
                            .name(item.getProduct().getName())
                            .imageUrl(item.getProduct().getImage())
                            .price(item.getProduct().getPrice())
                            .quantity(item.getQuantity())
                            .build())
                    .toList();

            // [개선!] DTO 의도에 맞게 orderName 동적 생성
            String orderName;
            if (productDtos.isEmpty()) {
                orderName = "주문 상품 정보 없음";
            } else if (productDtos.size() == 1) {
                orderName = productDtos.get(0).getName();
            } else {
                orderName = String.format("%s 외 %d개", productDtos.get(0).getName(), productDtos.size() - 1);
            }

            int totalQuantity = productDtos.stream()
                    .mapToInt(PurchaseHistoryResponse.ProductDto::getQuantity)
                    .sum();

            builder.saleLabel("판매")
                    .category(order.getProductPost().getCategory().getName())
                    .orderName(orderName) // 생성된 주문명 설정
                    .totalQuantity(totalQuantity)
                    .products(productDtos);

        }
        // 2. 중고 거래일 경우
        else if (order.getTradePost() != null) {
            var tradePost = order.getTradePost();

            PurchaseHistoryResponse.ProductDto productDto = PurchaseHistoryResponse.ProductDto.builder()
                    .name(tradePost.getTitle())
                    .imageUrl(tradePost.getThumbnailImage())
                    .price(payment.getAmount())
                    .quantity(1)
                    .build();

            builder.saleLabel("중고 거래")
                    .category(tradePost.getCategory().getName())
                    .orderName(tradePost.getTitle()) // [개선!] 중고거래는 게시물 제목을 그대로 주문명으로 사용
                    .totalQuantity(1)
                    .products(List.of(productDto));

        } else {
            throw new IllegalArgumentException("유효하지 않은 주문 타입입니다. Order ID: " + order.getId());
        }

        return builder.build();
    }
}