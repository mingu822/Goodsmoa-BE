package com.goodsmoa.goodsmoa_BE.product.DTO.Post;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
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

    @NotNull(message = "카테고리는 필수입니다.")
    private Integer categoryId;  // ✅ Category의 FK (카테고리 ID)

}
