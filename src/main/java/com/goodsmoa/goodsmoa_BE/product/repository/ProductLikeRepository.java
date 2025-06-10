package com.goodsmoa.goodsmoa_BE.product.repository;

import com.goodsmoa.goodsmoa_BE.product.entity.ProductLikeEntity;
import com.goodsmoa.goodsmoa_BE.product.entity.ProductPostEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductLikeRepository extends JpaRepository<ProductLikeEntity,Long> {

    boolean existsByProductPostEntityAndUser(ProductPostEntity productPost, UserEntity user);

    Optional<ProductLikeEntity> findByProductPostEntityAndUser(ProductPostEntity entity, UserEntity user);

    List<ProductLikeEntity> findByProductPostEntity(ProductPostEntity entity);
}
