package com.goodsmoa.goodsmoa_BE.trade.Entity;

import com.goodsmoa.goodsmoa_BE.category.Entity.Category;
import com.goodsmoa.goodsmoa_BE.user.Entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "trade_post")
public class TradePost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category categoryId;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private Integer productPrice;

    @Enumerated(EnumType.STRING)
    private ConditionStatus conditionStatus;

    @Enumerated(EnumType.STRING)
    private TradeStatus tradeStatus;

    private Boolean deliveryPrice;
    private Boolean direct;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String place;
    private Long views = 0L;

    private String hashtag;


    @Builder
    public TradePost(User userId, Category categoryId, String title, String content, Integer productPrice,
                     ConditionStatus conditionStatus, TradeStatus tradeStatus, Boolean deliveryPrice, Boolean direct,
                     String place, String hashtag) {
        this.userId = userId;
        this.categoryId = categoryId;
        this.title = title;
        this.content = content;
        this.productPrice = productPrice;
        this.conditionStatus = conditionStatus;
        this.tradeStatus = tradeStatus;
        this.deliveryPrice = deliveryPrice;
        this.direct = direct;
        this.place = place;
        this.hashtag = hashtag;
        this.createdAt = LocalDateTime.now();
        this.views = 0L;
    }
    /** ✅ 조회수 증가 */
    public void increaseViews() {
        this.views += 1;
    }

    /** ✅ 거래 상태 변경 */
    public void changeTradeStatus(TradeStatus newStatus) {
        this.tradeStatus = newStatus;
        this.updatedAt = LocalDateTime.now();
    }

    /** ✅ 게시글 내용 수정 */
    public void updatePost(String newTitle, String newContent, Integer newProductPrice, String newHashtag) {
        this.title = newTitle;
        this.content = newContent;
        this.productPrice = newProductPrice;
        this.hashtag = newHashtag;
        this.updatedAt = LocalDateTime.now();
    }

    /** ✅ 거래 방식 수정 (택배비 & 직거래 가능 여부 변경) */
    public void updateTradeOptions(Boolean newDeliveryPrice, Boolean newDirect) {
        this.deliveryPrice = newDeliveryPrice;
        this.direct = newDirect;
        this.updatedAt = LocalDateTime.now();
    }

    /** ✅ 거래 위치 수정 */
    public void updateTradeLocation(String newPlace) {
        this.place = newPlace;
        this.updatedAt = LocalDateTime.now();
    }
    public enum ConditionStatus {
        중고, 새상품, 교환
    }
    public enum TradeStatus {
        판매중, 거래중, 완료

    }
}
