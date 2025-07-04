package com.goodsmoa.goodsmoa_BE.commission.converter;

import com.goodsmoa.goodsmoa_BE.category.Entity.Category;
import com.goodsmoa.goodsmoa_BE.commission.dto.detail.CommissionDetailRequest;
import com.goodsmoa.goodsmoa_BE.commission.dto.detail.CommissionDetailResponse;
import com.goodsmoa.goodsmoa_BE.commission.dto.post.*;
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
    public CommissionPostEntity saveToEntity(PostRequest request, UserEntity user, Category category) {
        return CommissionPostEntity.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .thumbnailImage(request.getThumbnailImage())
                .requestLimited(request.getRequestLimited())
                .minimumPrice(request.getMinimumPrice())
                .maximumPrice(request.getMaximumPrice())
                .createdAt(LocalDateTime.now())
                .hashtag(request.getHashtag())
                .category(category)
                .status(true)
                .views(0L)
                .likes(0L)
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
                .content(entity.getContent())
                .thumbnailImage(entity.getThumbnailImage())
                .build();
    }

    public PostResponse toResponse(CommissionPostEntity entity) {
        return PostResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .thumbnailImage(entity.getThumbnailImage())
                .userName(entity.getUser().getNickname())
                .userImage(entity.getUser().getImage())
                .views(entity.getViews())
                .hashtag(entity.getHashtag())
                .build();
    }

    public PostDetailResponse detailPostToResponse(CommissionPostEntity increaseEntity, List<CommissionDetailEntity> detailEntities) {
        return PostDetailResponse.builder()
                .id(increaseEntity.getId())
                .title(increaseEntity.getTitle())
                .content(increaseEntity.getContent())
                .categoryName(increaseEntity.getCategory().getName())
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
