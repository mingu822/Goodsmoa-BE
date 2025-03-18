package com.goodsmoa.goodsmoa_BE.trade.Entity;

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

    private String imagePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trade_id")
    private TradePostEntity tradePostEntity;

    // 이미지를 변경하는 메서드
    public void updateImagePath(String newImagePath) {
        this.imagePath = newImagePath;
    }


}
