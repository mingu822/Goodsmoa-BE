package com.goodsmoa.goodsmoa_BE.community.dto;

//프론트에서 서버로 보내는 댓글 request dto


import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityReplyRequest {

    private Long postId;         // 댓글이 달릴 게시글 ID

    private Long parentId;       // 부모 댓글 ID (없으면 null → 최상위 댓글)

    private String content;      // 댓글 내용
}