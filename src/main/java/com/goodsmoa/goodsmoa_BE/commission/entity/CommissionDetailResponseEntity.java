package com.goodsmoa.goodsmoa_BE.commission.entity;

import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "commission_detail_response")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommissionDetailResponseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "detail_id", nullable = false)
    private CommissionDetailEntity commissionDetailEntity;

    @ManyToOne
    @JoinColumn(name = "subscription_id", nullable = false)
    private CommissionSubscriptionEntity subscription;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Setter
    @Column(name = "res_content", columnDefinition = "LONGTEXT")
    private String resContent;
}
