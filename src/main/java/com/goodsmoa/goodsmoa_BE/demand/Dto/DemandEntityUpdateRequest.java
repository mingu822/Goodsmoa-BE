package com.goodsmoa.goodsmoa_BE.demand.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder @Getter
@NoArgsConstructor
@AllArgsConstructor
public class DemandEntityUpdateRequest {
    private Long id;

    @NotBlank(message = "제목을 작성해주십시오")
    @Size(min = 2, max = 50)
    private String title;

    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String image;
    private int state;
    private LocalDateTime createdAt;
    private LocalDateTime pullAt;
}
