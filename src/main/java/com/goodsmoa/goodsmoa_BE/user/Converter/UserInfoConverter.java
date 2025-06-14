package com.goodsmoa.goodsmoa_BE.user.Converter;

import com.goodsmoa.goodsmoa_BE.user.DTO.AccountResponseDto;
import com.goodsmoa.goodsmoa_BE.user.DTO.AddressResponseDto;
import com.goodsmoa.goodsmoa_BE.user.DTO.UserInfo;
import com.goodsmoa.goodsmoa_BE.user.DTO.UserInfoResponseDto;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserAccountEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserAddressEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class UserInfoConverter {

    public static UserInfoResponseDto toDto(UserEntity user, List<UserAddressEntity> addressList, UserAccountEntity account) {

        //  주소 변환을 AddressConverter로 축약!
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

    public static UserInfo toUserInfo(UserEntity userEntity) {
        if (userEntity == null) {
            return null; // 또는 필요에 따라 빈 UserInfo 객체 반환
        }
        return UserInfo.builder()
                .id(userEntity.getId())
                .nickname(userEntity.getNickname()) // UserEntity의 닉네임 필드가 nickname이라고 가정합니다.
                .image(userEntity.getImage()) // UserEntity의 프로필 이미지 URL 필드가 있다고 가정합니다.
                .build();
    }
}
