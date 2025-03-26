package com.goodsmoa.goodsmoa_BE.Qna.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class AnswerDeleteResponseDto {
    private Long id;

    private String title;
    private String reqContent;
    private String resContent;
    private LocalDateTime resCreatedAt; // ✅ 답변 작성 시간
    private LocalDateTime resupdatedAt;  //답변 업데이트 시간
}
