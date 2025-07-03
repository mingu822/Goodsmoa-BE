package com.goodsmoa.goodsmoa_BE.commission.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "commission_detail")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommissionDetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "commission_id", nullable = false)
    private CommissionPostEntity commissionPostEntity;

    @Setter
    @Column(nullable = false, length = 200)
    private String title;

    @Setter
    @Column(name = "req_content", nullable = false, length = 200)
    private String reqContent;

    @Column(name = "res_content", columnDefinition = "LONGTEXT")
    private String resContent;
}