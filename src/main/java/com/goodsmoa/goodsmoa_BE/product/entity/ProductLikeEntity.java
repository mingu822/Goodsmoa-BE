package com.goodsmoa.goodsmoa_BE.product.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product_like",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"post_id", "user_id"})
        }
)
public class ProductLikeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false) // 리뷰와의 관계
    @JsonBackReference
    private ProductPostEntity productPostEntity;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // 리뷰와의 관계
    @JsonBackReference
    private UserEntity user;

}
