package com.goodsmoa.goodsmoa_BE.product.DTO.Post;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {

    private Long id;  // ✅ ProductPost 의 PK (게시글 ID)

    private String title;

    private String content;

    private LocalDateTime createdAt;

    private String thumbnailImage;

    private Boolean isPublic;

    private LocalDate startTime;

    private LocalDate endTime;

    private Boolean state;

    private String password;

    private Long views;

    private String hashtag;

    private String categoryName;

    private String userId;

}
