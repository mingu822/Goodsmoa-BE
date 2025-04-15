package com.goodsmoa.goodsmoa_BE.user.DTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class UserInfoResponseDto {
    private String id;          // 유저 ID
    private String name;        // 이름
    private String nickname;    // 닉네임
    private String image;       // 프로필 이미지
    private String content;     // 소개글
    private String email;  //이메일
    private String phoneNumber; // 전화번호
    private Boolean identity;   // 본인 인증 여부
    private Integer penalty;    // 패널티 횟수
    private Boolean status;     // 계정 활성 상태
    private Integer reportCount;// 신고 횟수
    private String role;        // 역할

    private List<AddressResponseDto> addresses;
    private AccountResponseDto account;
}
