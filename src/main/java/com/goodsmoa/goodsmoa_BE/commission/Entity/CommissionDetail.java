package com.goodsmoa.goodsmoa_BE.commission.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "commission_detail")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommissionDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "commission_id", nullable = false)
    private CommissionPost commissionId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "req_content", nullable = false, length = 200)
    private String reqContent;

    @Column(name = "res_content", columnDefinition = "LONGTEXT")
    private String resContent;
}