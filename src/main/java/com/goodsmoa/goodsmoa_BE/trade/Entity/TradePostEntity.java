package com.goodsmoa.goodsmoa_BE.trade.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.goodsmoa.goodsmoa_BE.category.Entity.Category;
import com.goodsmoa.goodsmoa_BE.trade.DTO.Post.TradePostRequest;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "trade_post")
@Builder
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class TradePostEntity {
//
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id" , nullable = false)
    private Category category;

    private String title;

//    @Setter
//    @Column(columnDefinition = "MEDIUMTEXT")
//    private String content;

    @OneToMany(mappedBy = "tradePost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TradePostDescription> contentDescriptions = new ArrayList<>();

    private Integer productPrice;

    @Enumerated(EnumType.STRING)
    private ConditionStatus conditionStatus;

    @Enumerated(EnumType.STRING)
    private TradeStatus tradeStatus;

    private Boolean delivery;

    private Long deliveryPrice;

    private Boolean direct;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    @Setter
    @Column(name = "thumbnail_image" )
    private String thumbnailImage;

    private String place;

    @Column(nullable = false)
    private Long views =0L;

    private String hashtag;

    private LocalDateTime pulledAt;

    @OneToMany(mappedBy = "tradePostEntity", fetch = FetchType.LAZY , cascade = CascadeType.ALL , orphanRemoval = true)
    private List<TradeImageEntity> image;

    @OneToMany(mappedBy = "tradePost", fetch = FetchType.LAZY)
    private List<UserHiddenPost> hiddenByUsers;

    public boolean isHiddenByUser(UserEntity user){
        return hiddenByUsers.stream().anyMatch(hiddenPost ->hiddenPost.getUser().equals(user));
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }


    // 이미지를 변경하는 메서드
    public void addImageList(List<TradeImageEntity> updateImages) {
        if (this.image == null) {
            this.image = new ArrayList<>(); // 리스트가 null이면 새로 생성
        }
        for(TradeImageEntity img : updateImages) {
            img.setTradePostEntity(this);
        }
        this.image.addAll(updateImages); // 새로운 이미지 추가
    }
    public void addImage(TradeImageEntity updateImage) {
        if (this.image == null) {
            this.image = new ArrayList<>();
        }
        updateImage.setTradePostEntity(this);
        this.image.add(updateImage);
    }

//    끌어올림 하는 메서드
    public void pullAt(LocalDateTime pulledAt) {
        this.pulledAt = pulledAt;

    }

    /** ✅ 조회수 증가 */
    public void getViews(Long views){
        this.views += views;
    }

    /** ✅ 게시글 내용 수정 */
    public void updatePost(TradePostRequest request) {
        if(request.getTitle() != null) this.title = request.getTitle();
//        this.content = contentWithImages;

        if(request.getProductPrice() < 0 ) {
            throw new IllegalArgumentException("가격은 음수가 될 수 없습니다.");
        }


        if(request.getHashtag() != null) this.hashtag = request.getHashtag();

//        if(request.getThumbnailImage() != null) this.thumbnailImage = request.getThumbnailImage();
        this.tradeStatus = request.getTradeStatus();
        this.conditionStatus = request.getConditionStatus();

    }

    /** ✅ 거래 방식 수정 (택배비 & 직거래 가능 여부 변경) */
    public void updateTradeOptions(TradePostRequest request) {
        if(request.getDelivery() != null) this.delivery = request.getDelivery();
        if(request.getDirect() != null) this.direct = request.getDirect();

    }

    /** ✅ 거래 위치 수정 */
    public void updateTradeLocation(TradePostRequest request) {
        if(request.getPlace() != null) this.place = request.getPlace();
    }

    public void updateThumbnailImage(String newThumbnailUrl) {
        this.thumbnailImage = newThumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailImage = thumbnailUrl;
    }


    public enum ConditionStatus {
        중고, 새상품, 교환
    }
    public enum TradeStatus {
        판매중, 거래중, 완료
    }



}

