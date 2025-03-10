package com.goodsmoa.goodsmoa_BE.trade.DTO;

import com.goodsmoa.goodsmoa_BE.trade.Entity.TradePost;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TradePostResponse {
    private Long id;
    private String title;
    private String content;
    private Integer productPrice;
    private String conditionStatus;
    private String tradeStatus;
    private Boolean deliveryPrice;
    private Boolean direct;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String place;
    private Long views;
    private String hashtag;

    public TradePostResponse(TradePost tradePost) {
        this.id = tradePost.getId();
        this.title = tradePost.getTitle();
        this.content = tradePost.getContent();
        this.productPrice = tradePost.getProductPrice();
        this.conditionStatus = tradePost.getConditionStatus().name();
        this.tradeStatus = tradePost.getTradeStatus().name();
        this.deliveryPrice = tradePost.getDeliveryPrice();
        this.direct = tradePost.getDirect();
        this.createdAt = tradePost.getCreatedAt();
        this.updatedAt = tradePost.getUpdatedAt();
        this.place = tradePost.getPlace();
        this.views = tradePost.getViews();
        this.hashtag = tradePost.getHashtag();
    }
}

