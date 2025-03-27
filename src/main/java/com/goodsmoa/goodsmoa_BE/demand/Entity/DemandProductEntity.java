package com.goodsmoa.goodsmoa_BE.demand.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    @Column(nullable = false)
    private String image;

    // 목표 수량
    @Column(nullable = false)
    private int targetCount;
    
    // 상품이 속한 수요조사 폼 id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "demand_id", nullable = false)
    private DemandPostEntity demandPostEntity;
}
