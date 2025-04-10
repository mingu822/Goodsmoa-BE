package com.goodsmoa.goodsmoa_BE.product.entity;

import com.goodsmoa.goodsmoa_BE.category.Entity.Category;
import com.goodsmoa.goodsmoa_BE.product.dto.Post.PostRequest;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "product_post")
public class ProductPostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private long id;

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
    private UserEntity user;

    /** ✅ 조회수 증가 */
    public void increaseViews() {
        this.views += 1;
    }


    public void updateFromRequest(PostRequest request, Category category, boolean status) {
        if (request.getTitle() != null) this.title = request.getTitle();
        if (request.getContent() != null) this.content = request.getContent();
        if (request.getThumbnailImage() != null) this.thumbnailImage = request.getThumbnailImage();
        if (request.getStartTime() != null) this.startTime = request.getStartTime();
        if (request.getEndTime() != null) this.endTime = request.getEndTime();
        if (request.getIsPublic() != null) this.isPublic = request.getIsPublic();
        if (request.getHashtag() != null) this.hashtag = request.getHashtag();

        // 카테고리 업데이트
        if (category != null) this.category = category;

        // 상태 업데이트
        this.state = status;
    }

    public void getViews(Long views) {
        this.views += views;
    }
}
