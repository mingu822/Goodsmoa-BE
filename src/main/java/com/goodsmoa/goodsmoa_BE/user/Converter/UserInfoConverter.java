package com.goodsmoa.goodsmoa_BE.user.Converter;

import com.goodsmoa.goodsmoa_BE.user.DTO.AccountResponseDto;
import com.goodsmoa.goodsmoa_BE.user.DTO.AddressResponseDto;
import com.goodsmoa.goodsmoa_BE.user.DTO.UserInfoResponseDto;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserAccountEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserAddressEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;

import java.util.List;

public class UserInfoConverter {

    public static UserInfoResponseDto toDto(UserEntity user, List<UserAddressEntity> addressList, UserAccountEntity account) {

        // ✨ 주소 변환을 AddressConverter로 축약!
        List<AddressResponseDto> addresses = addressList.stream()
                //.map(address -> AddressConverter.toDto(address))
                .map(AddressConverter::toDto)
                .toList();

        // 계좌 변환은 여기서 계속 처리
        AccountResponseDto accountDto = account != null ? AccountResponseDto.builder()
                .accountName(account.getAccountName())
                .name(account.getName())
                .number(account.getNumber())
                .build() : null;

        return UserInfoResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .nickname(user.getNickname())
                .image(user.getImage())
                .content(user.getContent())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .identity(user.getIdentity())
                .penalty(user.getPenalty())
                .status(user.getStatus())
                .reportCount(user.getReportCount())
                .role(user.getRole())
                .addresses(addresses)
                .account(accountDto)
                .build();
    }
}
