package com.goodsmoa.goodsmoa_BE.demand.converter;

import com.goodsmoa.goodsmoa_BE.demand.dto.order.DemandOrderCreateRequest;
import com.goodsmoa.goodsmoa_BE.demand.dto.order.DemandOrderListResponse;
import com.goodsmoa.goodsmoa_BE.demand.dto.order.DemandOrderResponse;
import com.goodsmoa.goodsmoa_BE.demand.dto.post.DemandPostCreateRequest;
import com.goodsmoa.goodsmoa_BE.demand.dto.post.DemandProductResponse;
import com.goodsmoa.goodsmoa_BE.demand.entity.DemandOrderEntity;
import com.goodsmoa.goodsmoa_BE.demand.entity.DemandOrderProductEntity;
import com.goodsmoa.goodsmoa_BE.demand.entity.DemandPostEntity;
import com.goodsmoa.goodsmoa_BE.demand.entity.DemandPostProductEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DemandOrderConverter {

    private final DemandOrderProductConverter demandOrderProductConverter;

    public DemandOrderConverter(DemandOrderProductConverter demandOrderProductConverter) {
        this.demandOrderProductConverter = demandOrderProductConverter;
    }

    // 생성할때만 사용
    // DemandOrderRequest -> DemandOrderEntity
    public DemandOrderEntity toEntity(UserEntity user, DemandPostEntity postEntity) {
        return DemandOrderEntity.builder()
                .user(user) //주문한 회원
                .demandPost(postEntity) // 주문한 상품글
                .demandOrderProducts(new ArrayList<>()) // 주문한 제품리스트
                .build();
    }

    // DemandOrderEntity -> DemandOrderEntityResponse
    public DemandOrderResponse toResponse(DemandOrderEntity entity) {
        return DemandOrderResponse.builder()
                .id(entity.getId())
                .creatAt(entity.getCreatedAt())
                .user(entity.getUser())
                .postEntity(entity.getDemandPost())
                .demandOrderProducts(entity.getDemandOrderProducts().stream().map(demandOrderProductConverter::toResponse).toList())
                .build();
    }

    public DemandOrderListResponse toListResponse(DemandOrderEntity entity) {
        DemandPostEntity postEntity = entity.getDemandPost();

        return DemandOrderListResponse.builder()
                .id(entity.getId())
                .title(postEntity.getTitle())
                .imageUrl(postEntity.getImageUrl())
                .creatAt(entity.getCreatedAt())
                .build();
    }
}
