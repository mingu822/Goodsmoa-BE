package com.goodsmoa.goodsmoa_BE.product.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product_post")
public class ProductPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @Column(name = "content", columnDefinition = "LONGTEXT", nullable = false)
    private String content;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "thumbnail_image", length = 255, nullable = false)
    private String thumbnailImage;

    @Column(name = "public", nullable = false)
    private Boolean isPublic;

    @Column(name = "start_time", nullable = false)
    private LocalDate startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDate endTime;

    @Column(name = "state", nullable = false)
    private Boolean state;

    @Column(name = "password", length = 16)
    private String password;

    @Column(name = "views", nullable = false)
    private Long views = 0L;

    @Column(name = "hashtag", length = 150)
    private String hashtag;

    // 카테고리 만들면
    //@Column(name = "category_id", nullable = false)
    // private Integer categoryId;
}