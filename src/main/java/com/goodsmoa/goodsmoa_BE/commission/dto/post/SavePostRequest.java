package com.goodsmoa.goodsmoa_BE.commission.dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavePostRequest {

    private String title;

    private Boolean type;

    private String content;

    private String thumbnailImage;

    private LocalDateTime createdAt;

    private Boolean status;

    private Long views;
}