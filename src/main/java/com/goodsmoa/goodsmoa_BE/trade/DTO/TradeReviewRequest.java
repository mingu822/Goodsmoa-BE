package com.goodsmoa.goodsmoa_BE.trade.DTO;

import com.goodsmoa.goodsmoa_BE.trade.Entity.TradePost;
import com.goodsmoa.goodsmoa_BE.trade.Entity.TradeReview;
import com.goodsmoa.goodsmoa_BE.user.Entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class TradeReviewRequest {

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    private String file;

    @NotNull(message = "평점은 필수입니다.")
    private Double rating;

    private String content;

    // 빌더 패턴을 사용하여 Request DTO 생성
    public TradeReviewRequest(String title, String file,
                              Double rating, String content) {
        this.title = title;
        this.file = file;
        this.rating = rating;
        this.content = content;
    }

    // 엔티티 변환 메서드

    public TradeReview toEntity(User userEntity, TradePost tradeEntity) {
        return TradeReview.builder()
                .user(userEntity)  // UserEntity 객체 사용
                .trade(tradeEntity)  // TradeEntity 객체 사용
                .title(title)
                .file(file)
                .rating(rating)
                .content(content)
                .createdAt(LocalDateTime.now())  // createdAt은 생성 시점
                .build();
    }
}

