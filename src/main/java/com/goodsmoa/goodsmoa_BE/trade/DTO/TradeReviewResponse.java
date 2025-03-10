package com.goodsmoa.goodsmoa_BE.trade.DTO;

import com.goodsmoa.goodsmoa_BE.trade.Entity.TradePost;
import com.goodsmoa.goodsmoa_BE.trade.Entity.TradeReview;
import com.goodsmoa.goodsmoa_BE.user.Entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TradeReviewResponse {

    private Long id;
    private User userId;
    private TradePost tradeId;
    private String title;
    private String file;
    private Double rating;
    private String content;
    private String createdAt;
    private String updateAt;

    // 엔티티 객체를 DTO로 변환하는 생성자
    public TradeReviewResponse(TradeReview tradeReviewEntity) {
        this.id = tradeReviewEntity.getId();
        this.userId = tradeReviewEntity.getUser();  // UserEntity의 id로 변환
        this.tradeId = tradeReviewEntity.getTrade();  // TradeEntity의 id로 변환
        this.title = tradeReviewEntity.getTitle();
        this.file = tradeReviewEntity.getFile();
        this.rating = tradeReviewEntity.getRating();
        this.content = tradeReviewEntity.getContent();
        this.createdAt = tradeReviewEntity.getCreatedAt().toString();
        this.updateAt = (tradeReviewEntity.getUpdateAt() != null) ?
                tradeReviewEntity.getUpdateAt().toString() : null;
    }
}

