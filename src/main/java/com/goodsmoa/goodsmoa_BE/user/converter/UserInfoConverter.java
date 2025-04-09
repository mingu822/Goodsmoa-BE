package com.goodsmoa.goodsmoa_BE.user.converter;

import com.goodsmoa.goodsmoa_BE.Qna.Entity.UserQuestionEntity;
import com.goodsmoa.goodsmoa_BE.Qna.dto.QuestionCreateRequestDto;
import com.goodsmoa.goodsmoa_BE.user.DTO.UserInfoResponseDto;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;

public class UserInfoConverter {

    // 엔티티->dto변환
    public static UserInfoResponseDto toDto(UserEntity user) {
        return UserInfoResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .nickname(user.getNickname())
                .image(user.getImage())
                .content(user.getContent())
                .identity(user.getIdentity())
                .penalty(user.getPenalty())
                .status(user.getStatus())
                .reportCount(user.getReportCount())
                .role(user.getRole())
                .build();
    }


}
