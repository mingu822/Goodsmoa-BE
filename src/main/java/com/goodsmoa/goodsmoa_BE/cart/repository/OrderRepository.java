package com.goodsmoa.goodsmoa_BE.cart.repository;

import com.goodsmoa.goodsmoa_BE.cart.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    Optional<OrderEntity> findById(Long Id);

    Optional<OrderEntity> findByOrderCode(String orderCode);

    @Query("SELECT o FROM OrderEntity o " +
            "LEFT JOIN FETCH o.orderItems " +
            "LEFT JOIN FETCH o.productPost pp LEFT JOIN FETCH pp.category " +
            "LEFT JOIN FETCH o.tradePost tp LEFT JOIN FETCH tp.category " +
            "WHERE o.id = :orderId")
    Optional<OrderEntity> findByIdWithDetails(@Param("orderId") Long orderId);
}
