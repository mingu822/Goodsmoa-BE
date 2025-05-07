package com.goodsmoa.goodsmoa_BE.cart.converter;

import com.goodsmoa.goodsmoa_BE.cart.dto.OrderPHResponse;
import com.goodsmoa.goodsmoa_BE.cart.dto.OrderRequest;
import com.goodsmoa.goodsmoa_BE.cart.dto.OrderResponse;
import com.goodsmoa.goodsmoa_BE.cart.entity.OrderEntity;
import com.goodsmoa.goodsmoa_BE.cart.entity.OrderItemEntity;
import com.goodsmoa.goodsmoa_BE.product.entity.ProductDeliveryEntity;
import com.goodsmoa.goodsmoa_BE.product.entity.ProductEntity;
import com.goodsmoa.goodsmoa_BE.product.entity.ProductPostEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import org.springframework.stereotype.Component;

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
    public OrderPHResponse toOrderPHResponse(OrderEntity order) {
        return OrderPHResponse.builder()
                .id(order.getId())
                .userName(order.getUser().getName())
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
}