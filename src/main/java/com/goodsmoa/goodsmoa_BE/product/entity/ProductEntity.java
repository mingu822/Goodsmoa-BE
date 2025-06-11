package com.goodsmoa.goodsmoa_BE.product.entity;

import com.goodsmoa.goodsmoa_BE.product.dto.ProductRequest;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product")
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "name", length = 40, nullable = false)
    private String name;

    @Column(name = "price")
    private Integer price;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "image")
    private String image;

    @Enumerated(EnumType.STRING)
    @Column(name = "available", nullable = false)
    private AvailabilityStatus available;

    @Column(name = "max_quantity")
    private Integer maxQuantity;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private ProductPostEntity productPostEntity;

    // enum
    public enum AvailabilityStatus{
        판매중, 품절, 숨기기
    }

    public void updateFromRequest(ProductRequest request) {
        this.name = request.getName();
        this.price = request.getPrice();
        this.quantity = request.getQuantity();
        this.maxQuantity = request.getMaxQuantity();
//        if (request.getImage() != null) {
//            this.image = request.getImage();
//        }
        this.available = AvailabilityStatus.판매중;
    }
}
