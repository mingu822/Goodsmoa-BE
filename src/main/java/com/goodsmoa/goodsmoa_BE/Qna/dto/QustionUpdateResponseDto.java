package com.goodsmoa.goodsmoa_BE.Qna.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class QustionUpdateResponseDto {
    private Long id;

    private String title;       // 문의 제목

    private String reqContent;  // 문의 내용

    //문의 생성날짜
    private LocalDateTime reqCreatedAt;

    //문의 수정날짜
    private LocalDateTime reqUpdatedAt;

}
