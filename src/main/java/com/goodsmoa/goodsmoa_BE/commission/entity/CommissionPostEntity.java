package com.goodsmoa.goodsmoa_BE.commission.entity;

import com.goodsmoa.goodsmoa_BE.commission.dto.post.PostRequest;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;

@Entity
@Table(name = "commission_post")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommissionPostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    @Column(name = "request_limited")
    private Integer requestLimited;

    @Column(name = "minimum_price", length = 100)
    private Integer minimumPrice;

    @Column(name = "maximum_price", length = 100)
    private Integer maximumPrice;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "thumbnail_image", nullable = false, length = 200)
    private String thumbnailImage;

    @Column(nullable = false)
    private Boolean type; // true: 그림, false: 기타

    @Column(nullable = false)
    private Boolean status; // true: 신청 가능

    @Column(name = "views", nullable = false)
    private Long views;

    @Column(length = 150)
    private String hashtag;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @OneToMany(mappedBy = "commissionPostEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommissionDetailEntity> details;

    public void increaseViews() {
        this.views += 1;
    }

    public void updateFromRequest(PostRequest request, boolean status) {
        if (request.getId() != null) this.id = request.getId();
        if (request.getTitle() != null) this.title = request.getTitle();
        if (request.getContent() != null) this.content = request.getContent();
        if (request.getThumbnailImage() != null) this.thumbnailImage = request.getThumbnailImage();
        if (request.getRequestLimited() != null) this.requestLimited = request.getRequestLimited();
        if (request.getMinimumPrice() != null) this.minimumPrice = request.getMinimumPrice();
        if (request.getMaximumPrice() != null) this.maximumPrice = request.getMaximumPrice();
        if (request.getHashtag() != null) this.hashtag = request.getHashtag();
        if (request.getType() != null) this.type = request.getType();

        // 상태 업데이트
        this.status = status;
    }

}