package com.goodsmoa.goodsmoa_BE.user.Entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user_address")
public class UserAddressEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //수령자명
    @Column(name = "recipient_name", length = 15, nullable = false)
    private String recipientName;

    //전화번호
    @Column(name = "phone_number", length = 15, nullable = false)
    private String phoneNumber;

    //주소
    @Column(name = "main_address", length = 50, nullable = false)
    private String mainAddress;

    //상세주소
    @Column(name = "detailed_address", length = 60, nullable = false)
    private String detailedAddress;

    //우편번호
    @Column(name = "zip_code", nullable = false)
    private int zipCode;

    //배송메모
    @Column(name = "post_memo", length = 100)
    private String postMemo;

    //기본배송지
    @Column(name = "basic_address", nullable = false)
    private boolean basicAddress;

    //해당 유저
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
}
