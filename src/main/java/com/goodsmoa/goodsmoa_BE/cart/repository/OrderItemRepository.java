package com.goodsmoa.goodsmoa_BE.cart.repository;

import com.goodsmoa.goodsmoa_BE.cart.entity.OrderEntity;
import com.goodsmoa.goodsmoa_BE.cart.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItemEntity,Long> {

    List<OrderItemEntity> findByOrder(OrderEntity order);
}
