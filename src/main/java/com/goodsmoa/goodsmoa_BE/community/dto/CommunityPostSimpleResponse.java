package com.goodsmoa.goodsmoa_BE.community.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityPostSimpleResponse {
    private Long id;
    private String title;
    private String detailCategory;   // 후기, 정보 등
    private String categoryName;     // 아이돌, 애니 등
    private String nickname;
    private Long views;
    private String createdAt;
    private String updatedAt;
    private Long replyCount;
    private Long likeCount;
}
