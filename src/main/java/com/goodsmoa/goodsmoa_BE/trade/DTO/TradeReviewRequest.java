package com.goodsmoa.goodsmoa_BE.trade.DTO;

import com.goodsmoa.goodsmoa_BE.trade.Entity.TradePostEntity;
import com.goodsmoa.goodsmoa_BE.trade.Entity.TradeReviewEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;


@Getter
public class TradeReviewRequest {

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    private String file;

    @NotNull(message = "평점은 필수입니다.")
    private Double rating;

    private String content;

    public TradeReviewEntity toEntity(TradePostEntity tradeEntity, User userEntity) {
        return TradeReviewEntity.builder()
                .trade(tradeEntity)  // 거래 정보 설정
                .user(userEntity)    // 리뷰 작성자 설정
                .title(title)
                .file(file)          // 파일 필드 (저장 방식 확인 필요)
                .rating(rating)
                .content(content)
                .build();
    }

}

