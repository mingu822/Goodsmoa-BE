package com.goodsmoa.goodsmoa_BE.demand.Dto;

import com.goodsmoa.goodsmoa_BE.demand.Entity.DemandProductEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder @Getter
@NoArgsConstructor
@AllArgsConstructor
public class DemandEntityRequest {

    private Long id;

    @NotBlank(message = "제목을 작성해주십시오")
    @Size(max = 200)
    private String title;

    @NotBlank(message = "상세설명을 작성해주십시오")
    private String description;

    @NotBlank(message = "설문조사 시작일을 지정해주세요")
    private LocalDateTime startTime;

    @NotBlank(message = "설문조사 마감일을 지정해주세요")
    private LocalDateTime endTime;

    @Size(max=255)
    private String image;

    private String hashtag;

    @NotBlank(message = "상품은 최소 1개 이상 있어야 합니다")
    private List<DemandProductEntity> products;
}
