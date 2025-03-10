package com.goodsmoa.goodsmoa_BE.trade.Entity;

import com.goodsmoa.goodsmoa_BE.user.Entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "trade_review")
public class TradeReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ManyToOne 관계 반영: 사용자와 거래는 여러 개가 연결될 수 있음
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "trade_id", nullable = false)
    private TradePost trade;

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

    @Builder
    public TradeReview(Long id, User user, TradePost trade, String title, String file,
                       Double rating, String content, LocalDateTime createdAt, LocalDateTime updateAt) {
        this.id = id;
        this.user = user;
        this.trade = trade;
        this.title = title;
        this.file = file;
        this.rating = rating;
        this.content = content;
        this.createdAt = createdAt;
        this.updateAt = updateAt;
    }
}

