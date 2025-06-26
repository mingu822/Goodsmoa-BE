package com.goodsmoa.goodsmoa_BE.trade.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "trade_image")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // 기본 생성자
@Builder
@AllArgsConstructor
public class TradeImageEntity {

    @Id  // 기본 키를 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String imageUrl;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trade_id")
    @JsonIgnore
    private TradePostEntity tradePostEntity;

    public void setTradePostEntity(TradePostEntity tradePostEntity) {
        this.tradePostEntity = tradePostEntity;
    }


}

