package com.goodsmoa.goodsmoa_BE.Qna.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 🔥 문의 crate 시 응답 dto
 */
@Data
@AllArgsConstructor
@Builder
public class QuestionCreateResponseDto {

    private Long id;            // 문의글 ID 추가

    // 문의 제목
    private String title;

    // 문의 내용
    private String reqContent;

    //문의 생성날짜
    private LocalDateTime reqCreatedAt;
}
