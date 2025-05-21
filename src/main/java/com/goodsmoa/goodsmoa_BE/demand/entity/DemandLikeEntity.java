package com.goodsmoa.goodsmoa_BE.demand.entity;

import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "demand_like")
public class DemandLikeEntity {
    //한 글에 같은 유저가 여러번 할 수 없음
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // 좋아요 한 유저
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    // 좋아요 한 글
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "demand_id")
    private DemandPostEntity demand;
}
