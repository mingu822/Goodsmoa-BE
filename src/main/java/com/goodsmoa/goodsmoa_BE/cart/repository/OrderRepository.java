package com.goodsmoa.goodsmoa_BE.cart.repository;

import com.goodsmoa.goodsmoa_BE.cart.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    Optional<OrderEntity> findById(Long Id);


    Optional<OrderEntity> findByOrderCode(String orderId);
}
