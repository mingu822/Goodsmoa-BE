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
        return OrderPHResponse.builder()
                .id(order.getId())
                .userName(order.getUser().getNickname())
                .paidAt(payment.getPaidAt())
                .userPhone(order.getUser().getPhoneNumber())
                .postName(order.getProductPost().getTitle())
                .postThumbnail(order.getProductPost().getThumbnailImage())
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
                .recipientName(request.getRecipientName())
                .phoneNumber(request.getPhoneNumber())
                .zipCode(request.getZipCode())
                .mainAddress(request.getMainAddress())
                .detailedAddress(request.getDetailedAddress())
                .postMemo(request.getPostMemo())
                .orderCode(UUID.randomUUID().toString())
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
                .productsPrice(order.getProductsPrice())
                .deliveryPrice(order.getDeliveryPrice())
                .totalPrice(order.getTotalPrice())
                .postTitle(order.getTradePost().getTitle()) // TradePost에서 정보 가져오기
                .postThumbnail(order.getTradePost().getThumbnailImage()) // TradePost에서 정보 가져오기
                .sellerNickname(order.getTradePost().getUser().getNickname()) // TradePost에서 정보 가져오기
                .build();
    }

    public PurchaseHistoryResponse mapToDto(OrderEntity order, PaymentEntity payment) {

        List<PurchaseHistoryResponse.ProductDto> productDtos = order.getOrderItems().stream()
                .map(item -> PurchaseHistoryResponse.ProductDto.builder()
                        .name(item.getProduct().getName())
                        .imageUrl(item.getProduct().getImage()) // 가정
                        .price(item.getProduct().getPrice())
                        .quantity(item.getQuantity())
                        .build())
                .toList();

        String saleLabel;
        if (order.getProductPost() != null) {
            saleLabel = "판매";}
//         else if (order.getTradePost() != null) {
//            saleLabel = "중고 거래";}
        else {
            saleLabel = "알 수 없음";
        }

        int totalQuantity = productDtos.stream().mapToInt(PurchaseHistoryResponse.ProductDto::getQuantity).sum();

        return PurchaseHistoryResponse.builder()
                .orderId(order.getId())
                .orderCode(order.getOrderCode())
                .recipientName(order.getRecipientName())
                .saleLabel(saleLabel)
                .category(order.getProductPost().getCategory().getName())
                .status(order.getStatus().name())
                .orderName(payment.getOrderName())
                .totalQuantity(totalQuantity)
                .totalPrice(payment.getAmount())
                .paymentDate(payment.getPaidAt())
                .products(productDtos)
                .build();
    }
}