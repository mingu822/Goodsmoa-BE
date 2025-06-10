package com.goodsmoa.goodsmoa_BE.product.repository;

import com.goodsmoa.goodsmoa_BE.product.entity.ProductPostEntity;
import com.goodsmoa.goodsmoa_BE.product.entity.ProductReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductReviewRepository extends JpaRepository<ProductReviewEntity, Long> {
    List<ProductReviewEntity> findByProductPostEntity(ProductPostEntity productPostEntity);
}
