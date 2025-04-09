package com.goodsmoa.goodsmoa_BE.user.DTO;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInfoResponseDto {
    private String id;
    private String name;
    private String email;
    private String phoneNumber;
    private String nickname;
    private String image;
    private String content;
    private Boolean identity;
    private Integer penalty;
    private Boolean status;
    private Integer reportCount;
    private String role;
}
