package com.goodsmoa.goodsmoa_BE.commission.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "commission_like",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"commission_id", "user_id"})
        }
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommissionLikeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "commission_id", nullable = false)
    @JsonBackReference
    private CommissionPostEntity commissionId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private UserEntity userId;

}
