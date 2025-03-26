package com.goodsmoa.goodsmoa_BE.Qna.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

//문의글 수정할때 보내는 dto
//필드:문의글 아아디,  제목,  내용

@Data
@AllArgsConstructor
@Builder
public class QuestionUpdateRequestDto {
    private String title;       // 문의 제목
    private String reqContent;  // 문의 내용
}
