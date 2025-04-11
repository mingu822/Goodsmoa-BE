package com.goodsmoa.goodsmoa_BE.user.DTO;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AccountResponseDto {
    private String accountName; // 은행명
    private String name;        // 예금주명
    private String number;      // 계좌번호
}
