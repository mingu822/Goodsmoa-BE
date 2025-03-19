package com.goodsmoa.goodsmoa_BE.product.Entity;

import com.goodsmoa.goodsmoa_BE.category.Entity.Category;
import com.goodsmoa.goodsmoa_BE.product.DTO.Post.PostRequest;
import com.goodsmoa.goodsmoa_BE.user.Entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "product_post")
public class ProductPostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @Column(name = "content", columnDefinition = "LONGTEXT", nullable = false)
    private String content;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "thumbnail_image" , nullable = false)
    private String thumbnailImage;

    @Column(name = "public")
    private Boolean isPublic;

    @Column(name = "start_time")
    private LocalDate startTime;

    @Column(name = "end_time")
    private LocalDate endTime;

    @Column(name = "state", nullable = false)
    private Boolean state = false;  // 기본값 false (임시저장)

    @Column(name = "password", length = 16)
    private String password;

    @Column(name = "views", nullable = false)
    private Long views = 0L;  // final 제거 (DB에서 기본값 설정)

    @Column(name = "hashtag", length = 150)
    private String hashtag;

    @ManyToOne
    @JoinColumn(name = "category_id")  // null 저장 방지
    private Category category;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "productPostEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductEntity> products;

    @OneToMany(mappedBy = "productPostEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductDeliveryEntity> delivers;


    // 요청 정보를 기반으로 업데이트하는 메서드
    public void updateFromRequest(PostRequest request,Category category, boolean status) {
        this.title = request.getTitle();
        this.content = request.getContent();
        this.thumbnailImage = request.getThumbnailImage();
        this.startTime = request.getStartTime();
        this.endTime = request.getEndTime();
        this.isPublic = request.getIsPublic();
        this.hashtag = request.getHashtag();
        this.category = category;
        this.state = status;
    }
}
