package com.goodsmoa.goodsmoa_BE.commission.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.goodsmoa.goodsmoa_BE.user.Entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "commission_review",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"commission_id", "user_id"})
        }
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommissionReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "commission_id", nullable = false)
    @JsonBackReference
    private CommissionPost commissionId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User userId;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(length = 255)
    private String file;

    @Column(nullable = false)
    private Double rating;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "update_at")
    private LocalDateTime updatedAt;
}
