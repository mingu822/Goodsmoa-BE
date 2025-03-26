//(문의글 생성 요청 DTO)


package com.goodsmoa.goodsmoa_BE.Qna.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * 🔥 문의 생성 & 수정 시 요청을 받을 DTO (Data Transfer Object)
 */
@Data
@AllArgsConstructor
@Builder
public class QuestionCreateRequestDto {
    private String title;       // 문의 제목
    private String reqContent;  // 문의 내용
}
