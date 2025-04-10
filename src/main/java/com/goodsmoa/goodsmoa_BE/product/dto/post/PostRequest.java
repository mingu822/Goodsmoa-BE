package com.goodsmoa.goodsmoa_BE.product.dto.Post;

import com.goodsmoa.goodsmoa_BE.product.dto.Delivery.ProductDeliveryRequest;
import com.goodsmoa.goodsmoa_BE.product.dto.ProductRequest;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.*;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostRequest {

    private Long id;

    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 100, message = "제목은 최대 100자까지 가능합니다.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    private String content;

    @NotBlank(message = "썸네일 이미지는 필수입니다.")
    @Size(max = 255, message = "썸네일 이미지 URL은 최대 255자까지 가능합니다.")
    private String thumbnailImage;

    @NotNull(message = "공개 여부는 필수입니다.")
    private Boolean isPublic;

    @NotNull(message = "시작 날짜는 필수입니다.")
    private LocalDate startTime;

    @NotNull(message = "종료 날짜는 필수입니다.")
    private LocalDate endTime;

    @NotNull(message = "게시글 상태는 필수입니다.")
    private Boolean state;

    @Size(max = 16, message = "비밀번호는 최대 16자까지 가능합니다.")
    private String password;

    @Size(max = 150, message = "해시태그는 최대 150자까지 가능합니다.")
    private String hashtag;

    @NotNull(message = "카테고리는 필수입니다.")
    private Integer categoryId;  // ✅ Category의 FK (카테고리 ID)

    private List<ProductRequest> products;

    private List<ProductDeliveryRequest> delivers;
}