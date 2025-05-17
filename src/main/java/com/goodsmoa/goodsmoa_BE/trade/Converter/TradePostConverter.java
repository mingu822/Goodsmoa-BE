package com.goodsmoa.goodsmoa_BE.trade.Converter;

import com.goodsmoa.goodsmoa_BE.category.Entity.Category;
import com.goodsmoa.goodsmoa_BE.trade.DTO.Image.TradeImgUpdateRequest;
import com.goodsmoa.goodsmoa_BE.trade.DTO.Post.*;
import com.goodsmoa.goodsmoa_BE.trade.Entity.TradeImageEntity;
import com.goodsmoa.goodsmoa_BE.trade.Entity.TradePostEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TradePostConverter {

    private final TradeImageConverter tradeImageConverter;

    public TradePostConverter(TradeImageConverter tradeImageConverter) {
        this.tradeImageConverter = tradeImageConverter;
    }

    // Entity → Response 변환
    public TradePostResponse toResponse(TradePostEntity entity, List<TradeImageEntity> imageEntity) {
        return TradePostResponse.builder()
                .id(entity.getId())
                .user(entity.getUser())
                .title(entity.getTitle())
                .content(entity.getContent())
                .productPrice(entity.getProductPrice())
                .conditionStatus(entity.getConditionStatus())
                .tradeStatus(entity.getTradeStatus())
                .delivery(entity.getDelivery())
                .direct(entity.getDirect())
                .place(entity.getPlace())
                .thumbnailImage(entity.getThumbnailImage())
                .createdAt(entity.getCreatedAt())
                .hashtag(entity.getHashtag())
                .deliveryPrice(entity.getDeliveryPrice())
                .views(entity.getViews())
                .categoryName(entity.getCategory().getName()) // 수정: getCategory().getId()
                .tradeImage(imageEntity.stream().map(tradeImageConverter::toResponse).toList())
                .build();
    }

    public TradePostUpdateResponse upResponse(TradePostEntity entity){
        return TradePostUpdateResponse.builder()
                .userId(entity.getUser().getId())
                .userNickName(entity.getUser().getNickname())
                .categoryName(entity.getCategory().getName())
                .conditionStatus(entity.getConditionStatus())
                .tradeStatus(entity.getTradeStatus())
                .delivery(entity.getDelivery())
                .content(entity.getContent())
                .updatedAt(LocalDateTime.now())
                .deliveryPrice(entity.getDeliveryPrice())
                .direct(entity.getDirect())
                .hashtag(entity.getHashtag())
                .tradeImage(entity.getImage().stream()
                        .map(img -> TradeImgUpdateRequest.builder()
                                .id(img.getId())
                                .imagePath(img.getImagePath())
                                .build())
                        .collect(Collectors.toList()))
                .productPrice(entity.getProductPrice())
                .thumbnailImage(entity.getThumbnailImage())
                .id(entity.getId())
                .place(entity.getPlace())
                .build();
    }

    // Request → Entity 변환
    public TradePostEntity toEntity(TradePostRequest request, Category category, UserEntity user, String thumbnailUrl,String contentWithImages) {
        return TradePostEntity.builder()
                .user(user)  // 수정: User 객체를 직접 전달
                .category(category)  // 수정: Category 객체를 직접 전달
                .title(request.getTitle())
                .content(contentWithImages)
                .productPrice(request.getProductPrice())
                .conditionStatus(request.getConditionStatus())
                .tradeStatus(request.getTradeStatus())
                .delivery(request.getDelivery())
                .thumbnailImage(thumbnailUrl)
                .deliveryPrice(request.getDeliveryPrice())
                .views(0L)
                .createdAt(LocalDateTime.now())
                .direct(request.getDirect())
                .place(request.getPlace())
                .hashtag(request.getHashtag())
                .build();
    }
    // Entity → DetailResponse 변환
    public TradePostDetailResponse detailResponse(TradePostEntity entity) {
        List<TradeImageEntity> images = entity.getImage();
        return TradePostDetailResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .hashtag(entity.getHashtag())
                .categoryName(entity.getCategory().getName())
                .nickName(entity.getUser().getNickname())
                .userId(entity.getUser().getId())
                .image(entity.getUser().getImage())
                .imageUrl(images)
                .thumbnailImage(entity.getThumbnailImage())
                .delivery(entity.getDelivery())
                .deliveryPrice(entity.getDeliveryPrice())
                .productPrice(entity.getProductPrice())
                .direct(entity.getDirect())
                .views(entity.getViews())
                .place(entity.getPlace())
                .build();
    }

    public TradePostPulledResponse pulledResponse(TradePostEntity entity) {
        return TradePostPulledResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .pulledAt(entity.getPulledAt())
                .build();
    }

    public TradePostLookResponse lookResponse(TradePostEntity entity) {
        return TradePostLookResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .createdAt(entity.getCreatedAt())
                .thumbnailImage(entity.getThumbnailImage())
                .views(entity.getViews())
                .hashtag(entity.getHashtag())
                .userId(entity.getUser().getId())
                .userNickName(entity.getUser().getNickname())
                .userImage(entity.getUser().getImage())
                .build();
    }
}

