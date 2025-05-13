package com.goodsmoa.goodsmoa_BE.community.entity;

import com.goodsmoa.goodsmoa_BE.category.Entity.Category;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "community_posts")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityPostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(length = 50, nullable = false)
    private String title;

    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String content;

    //상세 카테고리
    @Column(nullable = false)
    private String detailCategory; // 예: 잡담, 질문, 정보

    //조회수
    @Column
    private Long views;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;


    //조회수 증가 메서드
    public void incraseViews(Long count) {
        this.views = (this.views == null ? 0 : this.views) + count;
    }


    //커뮤니티 글 수정 메서드
    public void updatePost(String title, String content, String detailCategory) {
        if (title != null) {
            this.title = title;
        }
        if (content != null) {
            this.content = content;
        }
        if (detailCategory != null) {
            this.detailCategory = detailCategory;
        }

        this.updatedAt = LocalDateTime.now(); // 업데이트 시간은 무조건 갱신
    }


}
