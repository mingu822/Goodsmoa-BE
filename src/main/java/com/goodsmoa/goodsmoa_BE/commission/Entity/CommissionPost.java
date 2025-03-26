package com.goodsmoa.goodsmoa_BE.commission.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;

@Entity
@Table(name = "commission_post")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommissionPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    @Column(name = "request_limited", nullable = false)
    private int requestLimited;

    @Column(name = "minimum_price", nullable = false, length = 100)
    private String minimumPrice;

    @Column(name = "maximum_price", nullable = false, length = 100)
    private String maximumPrice;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "thumbnail_image", nullable = false, length = 200)
    private String thumbnailImage;

    @Column(nullable = false)
    private boolean type; // true: 그림, false: 기타

    @Column(nullable = false)
    private final boolean status = true; // true: 신청 가능

    @Column(name = "views", nullable = false)
    private final Long views = 0L;

    @Column(length = 150)
    private String hashtag;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userId;
}
