package com.goodsmoa.goodsmoa_BE.product.repository;

import com.goodsmoa.goodsmoa_BE.product.entity.ProductPostEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductPostRepository extends JpaRepository<ProductPostEntity, Long> {

    Page<ProductPostEntity> findAllByUser(UserEntity user, Pageable pageable);

}
