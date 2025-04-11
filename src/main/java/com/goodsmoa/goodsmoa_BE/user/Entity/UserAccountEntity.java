package com.goodsmoa.goodsmoa_BE.user.Entity;

import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user_account")
public class UserAccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 기본 키 (Auto Increment)

    // 계좌 소유자
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    // 은행명
    @Column(name = "account_name", length = 25, nullable = false)
    private String accountName;

    // 예금주명
    @Column(name = "name", length = 30, nullable = false)
    private String name;

    // 계좌번호
    @Column(name = "number", length = 100, nullable = false)
    private String number;
}
