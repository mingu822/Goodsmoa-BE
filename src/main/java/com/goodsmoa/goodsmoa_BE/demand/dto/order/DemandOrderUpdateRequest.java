package com.goodsmoa.goodsmoa_BE.demand.dto.order;

import com.goodsmoa.goodsmoa_BE.demand.entity.DemandOrderEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder @Getter
@NoArgsConstructor
@AllArgsConstructor
public class DemandOrderUpdateRequest {

    @NotBlank(message = "상품 목록에 최소 1개 이상 있어야 합니다")
    @Size(min = 1, message = "상품 목록에 최소 1개 이상 있어야 합니다")
    private List<DemandOrderProductRequest> products; //수정할 상품 목록
}
