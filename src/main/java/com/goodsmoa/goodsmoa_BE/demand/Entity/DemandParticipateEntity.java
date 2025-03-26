package com.goodsmoa.goodsmoa_BE.demand.Entity;


import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Entity @Getter
@Table(name = "demand_participate")
public class DemandParticipateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 구매를 원하는 개수
    @Column(nullable = false)
    private int count;

    // 수요조사 참여 회원
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    // 참여한 수요조사 글 id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "demand_id")
    private DemandEntity demand;

    // 구매를 원하는 상품 id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "demand_product_id")
    private DemandProductEntity demandProduct;
}
