package com.goodsmoa.goodsmoa_BE.Qna.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class AnswerCreateResponseDto {

    private Long id;
    private String resContent;
    private String title;
    private LocalDateTime resCreatedAt; // ✅ 답변 작성 시간
}
