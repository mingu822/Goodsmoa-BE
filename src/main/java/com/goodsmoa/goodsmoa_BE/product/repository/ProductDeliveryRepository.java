package com.goodsmoa.goodsmoa_BE.product.repository;

import com.goodsmoa.goodsmoa_BE.product.entity.ProductDeliveryEntity;
import com.goodsmoa.goodsmoa_BE.product.entity.ProductPostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductDeliveryRepository extends JpaRepository<ProductDeliveryEntity, Long> {

     List<ProductDeliveryEntity> findByProductPostEntity(ProductPostEntity postEntity);
}
