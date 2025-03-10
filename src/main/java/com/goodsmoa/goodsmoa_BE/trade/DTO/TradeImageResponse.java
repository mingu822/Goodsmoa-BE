package com.goodsmoa.goodsmoa_BE.trade.DTO;



import com.goodsmoa.goodsmoa_BE.trade.Entity.TradeImage;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TradeImageResponse {

    private Long id;
    private String imagePath;

    // 빌더 패턴을 사용하여 Response DTO 생성
    @Builder
    public TradeImageResponse(Long id, String imagePath) {
        this.id = id;
        this.imagePath = imagePath;
    }

    // 엔티티 객체를 Response DTO로 변환하는 메서드
    public TradeImageResponse(TradeImage tradeImage) {
        this.id = tradeImage.getId();
        this.imagePath = tradeImage.getImagePath();
    }
}
