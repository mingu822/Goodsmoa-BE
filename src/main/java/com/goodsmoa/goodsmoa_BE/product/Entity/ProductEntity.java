package com.goodsmoa.goodsmoa_BE.product.Entity;

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
@Table(name = "product")
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "name", length = 40, nullable = false)
    private String name;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "image", length = 255, nullable = false)
    private String image;

    @Enumerated(EnumType.STRING)
    @Column(name = "available", nullable = false)
    private AvailabilityStatus available;

    @Column(name = "max_quantity", nullable = false)
    private Integer maxQuantity;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private ProductPostEntity productPostEntity;

    // enum
    public enum AvailabilityStatus{
        판매중, 품절, 숨기기
    }
}
