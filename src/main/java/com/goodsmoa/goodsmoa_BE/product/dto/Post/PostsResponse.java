package com.goodsmoa.goodsmoa_BE.product.dto.Post;

import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostsResponse {

    private Long id;  // ✅ ProductPost 의 PK (게시글 ID)

    private String title;

    private LocalDateTime createdAt;

    private String thumbnailImage;

    private Long views;

    private String hashtag;

    private String userId;

    private String userNickName;

    private String userImage;

}
