package com.goodsmoa.goodsmoa_BE.product.Repository;

import com.goodsmoa.goodsmoa_BE.product.Entity.ProductEntity;
import com.goodsmoa.goodsmoa_BE.product.Entity.ProductPostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<ProductEntity,Long> {

    /**
     * 특정 상품글(postEntity)에 속한 모든 상품 리스트 조회
     */
    List<ProductEntity> findByProductPostEntity(ProductPostEntity postEntity);

}
