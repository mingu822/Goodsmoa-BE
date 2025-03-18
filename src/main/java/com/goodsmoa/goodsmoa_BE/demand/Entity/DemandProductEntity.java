package com.goodsmoa.goodsmoa_BE.demand.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity @Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "demand_product")
public class DemandProductEntity {

    // 수요조사 상품 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 상품명
    @Column(nullable = false, length = 50)
    private String name;

    // 가격
    @Column(nullable = false)
    private int price;

    // 이미지
    private String image;

    // 목표 수량
    @Column(nullable = false)
    private int targetCount;
    
    // 상품이 속한 수요조사 폼 id
    @ManyToOne
    @JoinColumn(name = "demand_id", nullable = false)
    private DemandEntity demandEntity;
}
