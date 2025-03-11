package com.goodsmoa.goodsmoa_BE.commission.Entity;

import com.goodsmoa.goodsmoa_BE.user.Entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "commission_response")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommissionResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User userId;

    @ManyToOne
    @JoinColumn(name = "commission_id", nullable = false)
    private CommissionPost commissionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_status")
    private RequestStatus requestStatus;

    public enum RequestStatus{
        확인중, 진행중, 완료, 거절
    }
}
