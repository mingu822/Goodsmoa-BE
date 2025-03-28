package com.goodsmoa.goodsmoa_BE.trade.DTO.Post;

import com.goodsmoa.goodsmoa_BE.category.Entity.Category;
import com.goodsmoa.goodsmoa_BE.trade.Entity.TradePostEntity;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TradePostRequest {

    @NotBlank(message = "이미지 첨부는 필수입니다.")
    private String thumbnailImage;

    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 50 , message = "제목은 최대 50자까지 가능합니다.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    private String content;

    @Min(value = 0, message = "가격은 0원 이상이어야 합니다.")
    private int productPrice;

    @NotNull(message = "상품 상태는 필수입니다.")
    private TradePostEntity.ConditionStatus conditionStatus;

    @NotNull(message = "거래 상태는 필수입니다.")
    private TradePostEntity.TradeStatus  tradeStatus;

    @NotNull(message = "배송 설정은 필수입니다.")
    private Boolean delivery;


    private long deliveryPrice;

    @NotNull(message = "직거래 가능 여부는 필수입니다.")
    private Boolean direct;

    private Integer categoryId;

    @Size(max = 100, message = "장소는 최대 100자까지 가능합니다.")
    private String place;


    private Long views = 0L;

    @Size(max = 150, message = "해시태그는 최대 150자까지 가능합니다.")
    private String hashtag;

    private List<String> imagePath;

}

