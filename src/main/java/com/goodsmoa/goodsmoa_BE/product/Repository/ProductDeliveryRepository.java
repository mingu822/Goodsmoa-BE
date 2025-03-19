package com.goodsmoa.goodsmoa_BE.product.Repository;

import com.goodsmoa.goodsmoa_BE.product.Entity.ProductDeliveryEntity;
import com.goodsmoa.goodsmoa_BE.product.Entity.ProductPostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductDeliveryRepository extends JpaRepository<ProductDeliveryEntity, Long> {

     List<ProductDeliveryEntity> findByProductPostEntity(ProductPostEntity postEntity);
}
