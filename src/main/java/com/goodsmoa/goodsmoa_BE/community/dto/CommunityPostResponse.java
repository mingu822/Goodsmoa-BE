package com.goodsmoa.goodsmoa_BE.community.dto;

import lombok.*;

import java.util.List;

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

    private String nickname;
    private Long views;
    private String createdAt;
    private String updatedAt;

    //해당글 총댓글수
    private Long replyCount;

    //댓글 트리
    private List<CommunityReplyResponse> replies;

}
