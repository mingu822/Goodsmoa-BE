package com.goodsmoa.goodsmoa_BE.user.Converter;

import com.goodsmoa.goodsmoa_BE.user.DTO.AddressRequestDto;
import com.goodsmoa.goodsmoa_BE.user.DTO.AddressResponseDto;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserAddressEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;


public class AddressConverter {

    // Dto -> entity 변환 메서드
    public static UserAddressEntity toEntity(AddressRequestDto dto, UserEntity user) {
        return UserAddressEntity.builder()
                .user(user)
                .recipientName(dto.getRecipientName())
                .phoneNumber(dto.getPhoneNumber())
                .mainAddress(dto.getMainAddress())
                .detailedAddress(dto.getDetailedAddress())
                .zipCode(dto.getZipCode())
                .postMemo(dto.getPostMemo())
                .basicAddress(dto.isBasicAddress())
                .build();
    }


    // entity →
    public static AddressResponseDto toDto(UserAddressEntity entity) {
        return AddressResponseDto.builder()
                .recipientName(entity.getRecipientName())
                .phoneNumber(entity.getPhoneNumber())
                .mainAddress(entity.getMainAddress())
                .detailedAddress(entity.getDetailedAddress())
                .zipCode(entity.getZipCode())
                .postMemo(entity.getPostMemo())
                .basicAddress(entity.isBasicAddress())
                .build();

    }
}
