package com.goodsmoa.goodsmoa_BE.trade.Entity;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TradePostDescription {

    private int id;

    @Enumerated(EnumType.STRING)
    private contentType contentType;

    private int value;

    public enum contentType{
        TEXT, IMAGE
    }
}
