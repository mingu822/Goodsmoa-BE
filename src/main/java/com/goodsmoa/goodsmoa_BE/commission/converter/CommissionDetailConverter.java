package com.goodsmoa.goodsmoa_BE.commission.converter;

import com.goodsmoa.goodsmoa_BE.commission.dto.detail.CommissionDetailRequest;
import com.goodsmoa.goodsmoa_BE.commission.dto.detail.CommissionDetailResponse;
import com.goodsmoa.goodsmoa_BE.commission.dto.post.PostDetailResponse;
import com.goodsmoa.goodsmoa_BE.commission.entity.CommissionDetailEntity;
import com.goodsmoa.goodsmoa_BE.commission.entity.CommissionDetailResponseEntity;
import com.goodsmoa.goodsmoa_BE.commission.entity.CommissionPostEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommissionDetailConverter {

    /**
     *  상세글을 위한 DTO -> entity 변경
     *  커미션을 받기 위한 상세 요청
     *  // TODO post_id와 커미션 둘 다 가져와서 오류 뜬 듯 고치셈
     */
    public CommissionDetailEntity detailToEntity(CommissionPostEntity postEntity, CommissionDetailRequest request) {
        return CommissionDetailEntity.builder()
                .commissionPostEntity(postEntity)
                .title(request.getTitle())
                .reqContent(request.getReqContent())
                .build();
    }

    public CommissionDetailResponse detailToResponse(CommissionDetailEntity saveEntity) {
        return CommissionDetailResponse.builder()
                .id(saveEntity.getId())
                .title(saveEntity.getTitle())
                .reqContent(saveEntity.getReqContent())
                .build();
    }

    public CommissionDetailResponseEntity detailResponseToEntity(UserEntity user, CommissionDetailEntity entity) {
        return CommissionDetailResponseEntity.builder()
                .user(user)
                .commissionDetailEntity(entity)
                .build();
    }
}
