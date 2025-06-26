package com.goodsmoa.goodsmoa_BE.trade.DTO.Post;

import com.goodsmoa.goodsmoa_BE.trade.DTO.Image.TradeImageResponse;
import com.goodsmoa.goodsmoa_BE.trade.Entity.TradePostEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime; // createdAt 같은 필드가 필요하면 추가
import java.util.List;

/**
 * 게시글 상세 조회 시 사용되는 응답 DTO
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TradePostDetailResponse {

    // 게시글 기본 정보
    private Long id;
    private String title;
    private String content; // ✅ HTML 본문 내용
    private String hashtag;
    private long productPrice;
    private long views;

    // 카테고리 정보
    private String categoryName;

    // 작성자 정보 (UserEntity 대신 필요한 정보만 선별)
    private String userId;
    private String nickName;
    private String userProfileImage;

    // 이미지 정보
    private String thumbnailImage; // 썸네일 이미지 URL
    private List<TradeImageResponse> productImages; // ✅ 하단 상품 이미지 목록 (DTO 리스트)

    // 거래 관련 정보
    private String place;
    private boolean delivery;
    private long deliveryPrice;
    private boolean direct;

    // TODO: 필요하다면 생성/수정 시간, 거래상태, 상품상태 등 필드 추가 가능
     private LocalDateTime createdAt;
     private TradePostEntity.TradeStatus tradeStatus;
     private TradePostEntity.ConditionStatus conditionStatus;
}