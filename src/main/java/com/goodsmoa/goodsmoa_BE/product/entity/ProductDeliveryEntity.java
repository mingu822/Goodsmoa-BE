package com.goodsmoa.goodsmoa_BE.product.entity;

import com.goodsmoa.goodsmoa_BE.product.dto.ProductRequest;
import com.goodsmoa.goodsmoa_BE.product.dto.delivery.ProductDeliveryRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product_delivery")
public class ProductDeliveryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "name", length = 30, nullable = false)
    private String name;

    @Column(name = "price", nullable = false)
    private Integer price;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private ProductPostEntity productPostEntity;

    public void updateFromRequest(ProductDeliveryRequest deliveryRequest) {
        this.name = deliveryRequest.getName();
        this.price = deliveryRequest.getPrice();

    }
}
