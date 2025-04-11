package com.goodsmoa.goodsmoa_BE.user.DTO;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AddressRequestDto {
    private String recipientName;
    private String phoneNumber;
    private String mainAddress;
    private String detailedAddress;
    private int zipCode;
    private String postMemo;
    private boolean basicAddress;
}
