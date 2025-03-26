package com.goodsmoa.goodsmoa_BE.Qna.converter;


import com.goodsmoa.goodsmoa_BE.Qna.Entity.UserQuestionEntity;
import com.goodsmoa.goodsmoa_BE.Qna.dto.QuestionUpdateRequestDto;
import com.goodsmoa.goodsmoa_BE.Qna.dto.QustionUpdateResponseDto;

// ✅ 요청 DTO → 엔티티 변환
public class QuestionUpdateConverter {

    // ✅ 요청 DTO → 엔티티 변환
    public static UserQuestionEntity toEntity(QuestionUpdateRequestDto request) {
        return UserQuestionEntity.builder()
                .title(request.getTitle())
                .reqContent(request.getReqContent())
                .build();
    }

    // ✅ 엔티티 → 응답 DTO 변환
    public static QustionUpdateResponseDto toDto(UserQuestionEntity entity) {
        return QustionUpdateResponseDto.builder()
                .title(entity.getTitle())
                .reqContent(entity.getReqContent())
                .reqCreatedAt(entity.getReqCreatedAt())
                .reqUpdatedAt(entity.getReqUpdatedAt())
                .id(entity.getId())
                .build();
    }
}
