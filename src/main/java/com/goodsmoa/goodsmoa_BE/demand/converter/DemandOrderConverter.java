package com.goodsmoa.goodsmoa_BE.demand.converter;

import com.goodsmoa.goodsmoa_BE.demand.dto.order.DemandOrderListResponse;
import com.goodsmoa.goodsmoa_BE.demand.dto.order.DemandOrderResponse;
import com.goodsmoa.goodsmoa_BE.demand.entity.DemandOrderEntity;
import com.goodsmoa.goodsmoa_BE.demand.entity.DemandPostEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@AllArgsConstructor
public class DemandOrderConverter {

    private final DemandOrderProductConverter demandOrderProductConverter;
    private final DemandPostConverter demandPostConverter;

    // 생성할때만 사용
    // DemandOrderRequest -> DemandOrderEntity
    public DemandOrderEntity toEntity(UserEntity user, DemandPostEntity postEntity) {
        return DemandOrderEntity.builder()
                .user(user) //주문한 회원
                .demandPostEntity(postEntity)
                .demandOrderProducts(new ArrayList<>()) // 주문한 제품리스트
                .build();
    }

    // DemandOrderEntity -> DemandOrderEntityResponse
    public DemandOrderResponse toResponse(DemandOrderEntity entity) {
        UserEntity user = entity.getUser();

        return DemandOrderResponse.builder()
                .id(entity.getId())
                .creatAt(entity.getCreatedAt())
                .userId(user.getId())
                .userNickName(user.getNickname())
                .userImage(user.getImage())
                .demandPostOmittedResponse(demandPostConverter.toOmittedResponse(entity.getDemandPostEntity()))
                .demandOrderProducts(entity.getDemandOrderProducts().stream().map(demandOrderProductConverter::toResponse).toList())
                .build();
    }
}
