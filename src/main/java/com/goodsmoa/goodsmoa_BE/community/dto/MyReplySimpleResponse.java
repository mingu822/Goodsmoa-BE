package com.goodsmoa.goodsmoa_BE.community.dto;


import lombok.*;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyReplySimpleResponse {
    private Long id;             // 댓글 ID
    private Long postId;         // 해당 댓글이 달린 게시글 ID
    private String content;      // 댓글 내용
    private String nickname;     // 작성자 닉네임 (사실상 본인)
    private String createdAt;
    private String updatedAt;
}
