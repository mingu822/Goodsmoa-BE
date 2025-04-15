package com.goodsmoa.goodsmoa_BE.user.Converter;

import com.goodsmoa.goodsmoa_BE.user.DTO.UserInfoResponseDto;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;

public class UserInfoConverter {


    //엔티티->dto변환
    // UserEntity → UserInfoResponseDto 변환
    public static UserInfoResponseDto toDto(UserEntity entity) {
        return UserInfoResponseDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .nickname(entity.getNickname())
                .image(entity.getImage())
                .content(entity.getContent())
                .identity(entity.getIdentity())
                .penalty(entity.getPenalty())
                .status(entity.getStatus())
                .reportCount(entity.getReportCount())
                .role(entity.getRole())
                .build();
    }
}
