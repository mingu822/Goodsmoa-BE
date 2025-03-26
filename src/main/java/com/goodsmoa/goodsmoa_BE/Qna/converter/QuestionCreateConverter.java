package com.goodsmoa.goodsmoa_BE.Qna.converter;


import com.goodsmoa.goodsmoa_BE.Qna.Entity.UserQuestionEntity;
import com.goodsmoa.goodsmoa_BE.Qna.dto.QuestionCreateRequestDto;
import com.goodsmoa.goodsmoa_BE.Qna.dto.QuestionCreateResponseDto;

public class QuestionCreateConverter {

    // ✅ 요청 DTO → 엔티티 변환 (Create용)
    public static UserQuestionEntity toEntity(QuestionCreateRequestDto request) {
        return UserQuestionEntity.builder()
                .title(request.getTitle())
                .reqContent(request.getReqContent())
                .build();
    }

    // ✅ 엔티티 → 응답 DTO 변환
    public static QuestionCreateResponseDto toDto(UserQuestionEntity entity) {
        return QuestionCreateResponseDto.builder()
                .title(entity.getTitle())
                .reqContent(entity.getReqContent())
                .reqCreatedAt(entity.getReqCreatedAt())
                .id(entity.getId())
                .build();
    }
}
