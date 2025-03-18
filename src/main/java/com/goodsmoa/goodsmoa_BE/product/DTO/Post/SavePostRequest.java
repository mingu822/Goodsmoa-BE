package com.goodsmoa.goodsmoa_BE.product.DTO.Post;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavePostRequest {

    private String title;

    private String content;

    private String thumbnailImage;

    private LocalDateTime createdAt;

    private Boolean state;

    private Long views;

}
