package com.goodsmoa.goodsmoa_BE.user.Entity;

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
@Table(name = "user_address")
public class UserAddressEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "address_name", length = 20)
    private String addressName;

    @Column(name = "phone_number", length = 15, nullable = false)
    private String phoneNumber;

    @Column(name = "recipient_name", length = 20, nullable = false)
    private String recipientName;

    @Column(name = "main_address", length = 20, nullable = false)
    private String mainAddress;

    @Column(name = "detailed_address", length = 20, nullable = false)
    private String detailedAddress;

    @Column(name = "basic_addrese", nullable = false)
    private Boolean basicAddress;

    @Column(name = "zip_code", nullable = false)
    private Integer zipCode;

    @Column(name = "post_memo", length = 100)
    private String postMemo;

}
