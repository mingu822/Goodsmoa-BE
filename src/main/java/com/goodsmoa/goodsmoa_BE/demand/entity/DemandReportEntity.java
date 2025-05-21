package com.goodsmoa.goodsmoa_BE.demand.entity;

import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity @Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "demand_report")
public class DemandReportEntity {

    // 수요조사 신고 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // 신고 제목
    @Column(nullable = false, length = 30)
    private String title;
    
    // 신고 내용
    @Column(nullable = false)
    private String content;
    
    // 신고 날짜
    @Column(nullable = false)
    private LocalDateTime createdAt;

    // 신고자의 ID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;

    // 신고된 폼 ID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "demand_id", nullable = false)
    private DemandPostEntity demandPostEntity;
}
