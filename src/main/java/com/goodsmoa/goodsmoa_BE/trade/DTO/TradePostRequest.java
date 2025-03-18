package com.goodsmoa.goodsmoa_BE.trade.DTO;

import com.goodsmoa.goodsmoa_BE.category.Entity.Category;
import com.goodsmoa.goodsmoa_BE.trade.Entity.TradePostEntity;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;



@Getter
@Builder
public class TradePostRequest {

    @NotNull
    private Long id;

    //TODO 바꾸기
    @NotNull(message = "카테고리 ID는 필수입니다.")
    private int categoryId;

    @NotBlank(message = "이미지 첨부는 필수입니다.")
    private String image;

    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 50 , message = "제목은 최대 50자까지 가능합니다.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    private String content;

    @NotNull(message = "상품 가격은 필수입니다.")
    @Min(value = 0, message = "가격은 0원 이상이어야 합니다.")
    private Integer productPrice;

    @NotBlank(message = "상품 상태는 필수입니다.")
    @Pattern(regexp = "중고|새상품|교환", message = "상품 상태는 '중고', '새상품', '교환' 중 하나여야 합니다.")
    private TradePostEntity.ConditionStatus conditionStatus;

    @NotBlank(message = "거래 상태는 필수입니다.")
    @Pattern(regexp = "판매중|거래중|완료", message = "거래 상태는 '판매중', '거래중', '완료' 중 하나여야 합니다.")
    private TradePostEntity.TradeStatus  tradeStatus;

    @NotNull(message = "배송비 설정은 필수입니다.")
    private Boolean deliveryPrice;

    @NotNull(message = "직거래 가능 여부는 필수입니다.")
    private Boolean direct;

    @Size(max = 100, message = "장소는 최대 100자까지 가능합니다.")
    private String place;

    @Size(max = 150, message = "해시태그는 최대 150자까지 가능합니다.")
    private String hashtag;

}
