package com.goodsmoa.goodsmoa_BE.commission.entity;

import com.goodsmoa.goodsmoa_BE.category.Entity.Category;
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

    @Setter
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

    @Setter
    @Column(name = "thumbnail_image", nullable = false, length = 200)
    private String thumbnailImage;

    @Column(nullable = false)
    private Boolean status; // true: 신청 가능

    @Setter
    @Column(name = "like_count")
    private Long likes = 0L;

    @Column(name = "views")
    private Long views = 0L;

    @Column(length = 150)
    private String hashtag;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "category_id")  // null 저장 방지
    private Category category;


    @OneToMany(mappedBy = "commissionPostEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommissionDetailEntity> details;

    public void increaseViews(Long views) {this.views += views;}

    public void updateFromRequest(PostRequest request, boolean status) {
        if (request.getId() != null) this.id = request.getId();
        if (request.getTitle() != null) this.title = request.getTitle();
        if (request.getRequestLimited() != null) this.requestLimited = request.getRequestLimited();
        if (request.getMinimumPrice() != null) this.minimumPrice = request.getMinimumPrice();
        if (request.getMaximumPrice() != null) this.maximumPrice = request.getMaximumPrice();
        if (request.getHashtag() != null) this.hashtag = request.getHashtag();

        // 상태 업데이트
        this.status = status;
    }

}