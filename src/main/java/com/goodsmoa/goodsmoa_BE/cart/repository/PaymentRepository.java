package com.goodsmoa.goodsmoa_BE.cart.repository;

import com.goodsmoa.goodsmoa_BE.cart.entity.OrderEntity;
import com.goodsmoa.goodsmoa_BE.cart.entity.PaymentEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {

    PaymentEntity findByOrder(OrderEntity order);

    Page<PaymentEntity> findByUserAndStatus(UserEntity user, PaymentEntity.PaymentStatus status, Pageable pageable);

    Optional<PaymentEntity> findByOrderAndStatus(OrderEntity order, PaymentEntity.PaymentStatus status);
}
