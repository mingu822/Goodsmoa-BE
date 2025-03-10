package com.goodsmoa.goodsmoa_BE.product.Entity;

import com.goodsmoa.goodsmoa_BE.user.Entity.User;
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
@Table(name = "product_like")
public class ProductLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id", nullable = false) // 리뷰와의 관계
    private User userId;

    @ManyToOne
    @JoinColumn(name = "id", nullable = false) // 리뷰와의 관계
    private ProductPost productId;

}
