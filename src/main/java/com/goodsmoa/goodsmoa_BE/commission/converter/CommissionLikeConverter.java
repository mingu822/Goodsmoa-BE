package com.goodsmoa.goodsmoa_BE.commission.converter;

import com.goodsmoa.goodsmoa_BE.commission.dto.like.CommissionLikeResponse;
import com.goodsmoa.goodsmoa_BE.commission.entity.CommissionLikeEntity;
import com.goodsmoa.goodsmoa_BE.commission.entity.CommissionPostEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class CommissionLikeConverter {

    public CommissionLikeEntity toEntity(CommissionPostEntity entity, UserEntity user) {
        return CommissionLikeEntity.builder()
                .commissionId(entity)
                .userId(user)
                .build();
    }


    public CommissionLikeResponse toResponse(CommissionLikeEntity commissionLikeEntity) {
        return CommissionLikeResponse.builder()
                .id(commissionLikeEntity.getId())
                .commissionId(commissionLikeEntity.getCommissionId().getId())
                .title(commissionLikeEntity.getCommissionId().getTitle())
                .thumbnailImage(commissionLikeEntity.getCommissionId().getThumbnailImage())
                .views(commissionLikeEntity.getCommissionId().getViews())
                .hashtag(commissionLikeEntity.getCommissionId().getHashtag())
                .userId(commissionLikeEntity.getUserId().getId())
                .userNickName(commissionLikeEntity.getUserId().getNickname())
                .userImage(commissionLikeEntity.getUserId().getImage())
                .build();

    }
}
