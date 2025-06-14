package com.goodsmoa.goodsmoa_BE.user.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {
    private String id; // 또는 Long id;
    private String nickname;
    private String image; // 프로필 사진 URL
}