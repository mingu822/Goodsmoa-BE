package com.goodsmoa.goodsmoa_BE.product.Entity;

import com.goodsmoa.goodsmoa_BE.user.Entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product_review")
public class ProductReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id", nullable = false) // 리뷰와의 관계
    private User userId;

    @ManyToOne
    @JoinColumn(name = "id", nullable = false) // 리뷰와의 관계
    private ProductPost productId;

    @Column(name = "title", length = 50, nullable = false)
    private String title;

    @Column(name = "file", length = 255)
    private String file;

    @Column(name = "rating", nullable = false)
    private Double rating;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "update_at")
    private LocalDateTime updatedAt;
}
