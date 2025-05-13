package com.goodsmoa.goodsmoa_BE.community.dto;

import lombok.*;
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityPostResponse {
    private Long id;
    private String title;
    private String content;
    // ex)잡답,정보..
    private String detailCategory;

    // ex) 애니, 아이돌..
    private String categoryName;

    //작성자 닉네임
    private String nickname;
    private Long views;
    private String createdAt;
}
