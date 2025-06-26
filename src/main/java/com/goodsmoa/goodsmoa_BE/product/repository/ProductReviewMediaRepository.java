package com.goodsmoa.goodsmoa_BE.product.repository;

import com.goodsmoa.goodsmoa_BE.product.entity.ProductReviewMediaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface ProductReviewMediaRepository extends JpaRepository<ProductReviewMediaEntity, Long> {
    void deleteAllByIdIn(Collection<Long> ids);
}
