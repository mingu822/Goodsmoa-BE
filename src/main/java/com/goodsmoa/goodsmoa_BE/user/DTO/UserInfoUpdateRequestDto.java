package com.goodsmoa.goodsmoa_BE.user.DTO;


import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class UserInfoUpdateRequestDto {
    private String name;
    private String nickname;
    private String email;
    private String phoneNumber;
    private String content;
    private String image;


    // 배송지를 여러 개 받을 수 있도록 리스트로!
    private List<AddressRequestDto> addresses;

    private AccountRequestDto account;
}
