package com.goodsmoa.goodsmoa_BE.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Board {
    ALL(0, "전체"),
    PRODUCT(1, "상품판매"),
    DEMAND(2, "수요조사"),
    TRADE(3, "중고거래"),
    COMMUNITY(4, "커뮤니티"),
    COMMISSION(5, "커미션");

    private final int id;
    private final String name;
}
