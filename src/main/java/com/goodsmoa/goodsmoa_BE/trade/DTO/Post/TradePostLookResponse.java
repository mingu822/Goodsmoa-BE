package com.goodsmoa.goodsmoa_BE.trade.DTO.Post;

import com.goodsmoa.goodsmoa_BE.trade.Entity.TradePostEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TradePostLookResponse {

    private Long id;  // ✅ ProductPost 의 PK (게시글 ID)

    private String title;

    private LocalDateTime createdAt;

    private String thumbnailImage;

    private Long views;

    private Integer productPrice;

    private String categoryName;

    private String hashtag;

    private String userId;

    private String userNickName;

    private String userImage;

    private TradePostEntity.TradeStatus tradeStatus;
}
