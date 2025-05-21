package com.goodsmoa.goodsmoa_BE.community.dto;

//커뮤니티 글 쓸때  프론트측에서 보내는 request dto
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityPostRequest {
    private String title;
    private String content;

    //ex)잡답,정보..
    private String detailCategory;
    //ex)애니,아이돌..
    private String categoryName;
}
