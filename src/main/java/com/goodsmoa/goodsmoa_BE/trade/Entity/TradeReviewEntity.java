package com.goodsmoa.goodsmoa_BE.trade.Entity;

import com.goodsmoa.goodsmoa_BE.user.Entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "trade_review")
@Builder
@AllArgsConstructor
public class TradeReviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ManyToOne 관계 반영: 사용자와 거래는 여러 개가 연결될 수 있음
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trade_id", nullable = false)
    private TradePostEntity trade;

    @Column(name = "title", nullable = false, length = 50)
    private String title;

    @Column(name = "file", length = 255)
    private String file;

    @Column(name = "rating", nullable = false)
    private Double rating;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "update_at")
    private LocalDateTime updateAt;


}

