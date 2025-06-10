package com.goodsmoa.goodsmoa_BE.cart.converter;

import com.goodsmoa.goodsmoa_BE.cart.dto.delivery.DeliveryResponse;
import com.goodsmoa.goodsmoa_BE.cart.dto.order.OrderItemResponse;
import com.goodsmoa.goodsmoa_BE.cart.entity.OrderEntity;
import com.goodsmoa.goodsmoa_BE.cart.entity.OrderItemEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DeliveryConverter {

    public DeliveryResponse toResponse(OrderEntity order, List<OrderItemEntity> orderItems) {
        List<OrderItemResponse> productResponses = orderItems.stream()
                .map(item -> OrderItemResponse.builder()
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .quantity(item.getQuantity())
                        .build())
                .collect(Collectors.toList());

        return DeliveryResponse.builder()
                .recipientName(order.getRecipientName())
                .phoneNumber(order.getPhoneNumber())
                .mainAddress(order.getMainAddress())
                .postMemo(order.getPostMemo())
                .products(productResponses)
                .totalCount(productResponses.size())
                .build();
    }

}
