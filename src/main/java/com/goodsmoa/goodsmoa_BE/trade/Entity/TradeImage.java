package com.goodsmoa.goodsmoa_BE.trade.Entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "trade_image")
@Getter
@NoArgsConstructor  // 기본 생성자
public class TradeImage {

    @Id  // 기본 키를 지정
    private Long id;

    private String imagePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trade_id")
    private TradePost tradepostid;

    // 빌더 패턴을 사용하여 객체 생성
    @Builder
    public TradeImage(Long id, String imagePath, TradePost tradepostid) {
        this.id = id;
        this.imagePath = imagePath;
        this.tradepostid = tradepostid;
    }

    // 이미지를 변경하는 메서드
    public void updateImagePath(String newImagePath) {
        this.imagePath = newImagePath;
    }


}
