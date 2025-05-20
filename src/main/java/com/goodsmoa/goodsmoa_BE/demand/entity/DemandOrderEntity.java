package com.goodsmoa.goodsmoa_BE.demand.entity;


import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minidev.json.annotate.JsonIgnore;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity @Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "demand_order")
public class DemandOrderEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 생성일시
    LocalDateTime createdAt = LocalDateTime.now();

    // 주문한 회원
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    // 주문한 수요조사 글 id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "demand_post_id")
    private DemandPostEntity demandPost;

    // 주문에 포함된 상품들
    @JsonIgnore
    @OneToMany(mappedBy = "demandOrderEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DemandOrderProductEntity> demandOrderProducts = new ArrayList<>();
}
