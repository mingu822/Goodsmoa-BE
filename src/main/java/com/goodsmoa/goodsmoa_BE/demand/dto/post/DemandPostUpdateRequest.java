package com.goodsmoa.goodsmoa_BE.demand.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Builder @Getter
@NoArgsConstructor
@AllArgsConstructor
public class DemandPostUpdateRequest {

    @NotBlank(message = "수정할 수요조사가 없습니다")
    private Long id;

    @NotBlank(message = "제목을 작성해주십시오")
    @Size(max = 200)
    private String title;

    @NotBlank(message = "상세설명을 작성해주십시오")
    private String description;

    @NotNull(message = "설문조사 시작일을 지정해주세요")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime startTime;

    @NotNull(message = "설문조사 마감일을 지정해주세요")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime endTime;

    @Size(max=255)
    private String imageUrl;

    private String hashtag;

    @NotBlank(message = "올바른 카테고리를 선택해주세요")
    private Integer categoryId;

    @NotBlank(message = "상품은 최소 1개 이상 있어야 합니다")
    private List<DemandPostProductRequest> products;
}
