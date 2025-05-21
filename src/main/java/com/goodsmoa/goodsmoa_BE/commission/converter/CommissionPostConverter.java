package com.goodsmoa.goodsmoa_BE.commission.converter;

import com.goodsmoa.goodsmoa_BE.commission.dto.detail.CommissionDetailRequest;
import com.goodsmoa.goodsmoa_BE.commission.dto.detail.CommissionDetailResponse;
import com.goodsmoa.goodsmoa_BE.commission.dto.post.PostDetailResponse;
import com.goodsmoa.goodsmoa_BE.commission.dto.post.PostResponse;
import com.goodsmoa.goodsmoa_BE.commission.dto.post.SavePostRequest;
import com.goodsmoa.goodsmoa_BE.commission.dto.post.SavePostResponse;
import com.goodsmoa.goodsmoa_BE.commission.entity.CommissionDetailEntity;
import com.goodsmoa.goodsmoa_BE.commission.entity.CommissionPostEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Component
public class CommissionPostConverter {

    private final CommissionDetailConverter commissionDetailConverter;

    /**
     * save DTO -> entity 변경
     * 커미션 상세를 만들기 위해 DB에 임시저장하기 위한 메서드
     */
    public CommissionPostEntity saveToEntity(SavePostRequest request, UserEntity user) {
        return CommissionPostEntity.builder()
                .title(request.getTitle())
                .type(request.getType())
                .content(request.getContent())
                .thumbnailImage(request.getThumbnailImage())
                .createdAt(LocalDateTime.now())
                .status(false)
                .views(0L)
                .user(user)
                .build();
    }

    /**
     * save DTO -> entity 변경
     * 저장한 임시정보를 불러오기 위해 사용
     */
    public SavePostResponse saveToResponse(CommissionPostEntity entity){
        return SavePostResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .type(entity.getType())
                .content(entity.getContent())
                .thumbnailImage(entity.getThumbnailImage())
                .build();
    }

    public PostResponse toResponse(CommissionPostEntity entity) {
        return PostResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .type(entity.getType())
                .content(entity.getContent())
                .requestLimited(entity.getRequestLimited())
                .minimumPrice(entity.getMinimumPrice())
                .maximumPrice(entity.getMaximumPrice())
                .hashtag(entity.getHashtag())
                .build();
    }

    public PostDetailResponse detailPostToResponse(CommissionPostEntity increaseEntity, List<CommissionDetailEntity> detailEntities) {
        return PostDetailResponse.builder()
                .id(increaseEntity.getId())
                .title(increaseEntity.getTitle())
                .type(increaseEntity.getType())
                .content(increaseEntity.getContent())
                .thumbnailImage(increaseEntity.getThumbnailImage())
                .requestLimited(increaseEntity.getRequestLimited())
                .minimumPrice(increaseEntity.getMinimumPrice())
                .maximumPrice(increaseEntity.getMaximumPrice())
                .hashtag(increaseEntity.getHashtag())
                .views(increaseEntity.getViews())
                .commissionDetail(detailEntities.stream().map(commissionDetailConverter::detailToResponse).toList())
                .build();
    }
}
