package com.goodsmoa.goodsmoa_BE.trade.Entity;


import com.goodsmoa.goodsmoa_BE.user.Entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "trade_like")
public class TradeLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ManyToOne 관계: 거래와 사용자는 각각 여러 개의 좋아요를 가질 수 있음
    @ManyToOne
    @JoinColumn(name = "trade_id", nullable = false)
    private TradePost trade;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}