package com.goodsmoa.goodsmoa_BE.trade.DTO.Post;

import com.goodsmoa.goodsmoa_BE.trade.Entity.TradeImageEntity;
import com.goodsmoa.goodsmoa_BE.trade.Entity.TradePostDescription;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TradePostDetailResponse {
    private Long id;

    private String title;

    private String hashtag;

    private String categoryName;

    private String nickName;

    private String image;

    private String userId;

    private String thumbnailImage;

    private List<TradeImageEntity> imageUrl;

    private List<TradePostDescription> description;

    private String place;

    private boolean delivery;

    private long deliveryPrice;

    private long productPrice;

    private boolean direct;

    private long views;
//    TODO 유저가 인증한 유저인지 확인하는 로직



}

