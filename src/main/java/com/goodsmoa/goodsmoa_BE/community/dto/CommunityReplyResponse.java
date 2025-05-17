package com.goodsmoa.goodsmoa_BE.community.dto;


//서버->프론트 댓글 response dto



import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityReplyResponse {
    private Long id;
    private Long postId;       // 댓글이 달린 게시글 ID
    private Long parentId;     // 부모 댓글 ID (없으면 null)
    private String content;
    private String nickname;
    private String createdAt;
    private String updatedAt;

    //트리구조를 위해 자식 댓글들 리스트 children으로 저장
    private List<CommunityReplyResponse> children = new ArrayList<>();
}