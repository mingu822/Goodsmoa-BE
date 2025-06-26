package com.goodsmoa.goodsmoa_BE.demand.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "demand_like", uniqueConstraints = @UniqueConstraint(columnNames = {"postId", "userId"}))
public class DemandLikeEntity {
    //한 글에 같은 유저가 여러번 할 수 없음
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long postId;

    private String userId;

    private LocalDateTime createdAt;

    public DemandLikeEntity() {}

    public DemandLikeEntity(String userId, Long postId) {
        this.userId = userId;
        this.postId = postId;
        this.createdAt = LocalDateTime.now();
    }
}
