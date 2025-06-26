package com.goodsmoa.goodsmoa_BE.product.repository;

import com.goodsmoa.goodsmoa_BE.product.entity.ProductPostEntity;
import com.goodsmoa.goodsmoa_BE.product.entity.ProductReviewEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import io.lettuce.core.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductReviewRepository extends JpaRepository<ProductReviewEntity, Long> {
    // 삭제용 (리스트 전체)
    List<ProductReviewEntity> findAllByProductPostEntity(ProductPostEntity post);

    // 조회용 (페이징 지원)
    Page<ProductReviewEntity> findByProductPostEntity(ProductPostEntity post, Pageable pageable);

    Optional<ProductReviewEntity> findByProductPostEntityAndUser(ProductPostEntity productPostEntity, UserEntity user);

    Page<ProductReviewEntity> findByUser(UserEntity user, Pageable pageable);
}
