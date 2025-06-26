package com.goodsmoa.goodsmoa_BE.product.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product_review",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"post_id", "user_id"})
        }
)
public class ProductReviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 구매자의 아이디
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // 리뷰와의 관계
    @JsonBackReference
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false) // 리뷰와의 관계
    @JsonBackReference
    private ProductPostEntity productPostEntity;

    @Column(name = "rating", nullable = false)
    private Double rating;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_at", nullable = false, updatable = false)
    private final LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "update_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductReviewMediaEntity> mediaList = new ArrayList<>();
}
