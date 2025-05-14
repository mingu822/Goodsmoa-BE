package com.goodsmoa.goodsmoa_BE.trade.DTO.Post;

import com.goodsmoa.goodsmoa_BE.trade.DTO.Image.TradeImgUpdateRequest;
import com.goodsmoa.goodsmoa_BE.trade.Entity.TradePostEntity;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradePostUpdateResponse {

    private Long id;

    private String content;

    private Integer productPrice;

    private TradePostEntity.ConditionStatus conditionStatus;

    private TradePostEntity.TradeStatus tradeStatus;

    private Boolean delivery;

    private long deliveryPrice;

    private String thumbnailImage;

    private Boolean direct;

    private String place;

    private String hashtag;

    private String categoryName;

    private String userId;

    private String userNickName;

    private LocalDateTime updatedAt;

    @Setter
    private List<String> contentImageUrls;

    private List<TradeImgUpdateRequest> tradeImage;
}
