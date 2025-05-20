package com.goodsmoa.goodsmoa_BE.trade.DTO.Post;

import com.goodsmoa.goodsmoa_BE.trade.Entity.TradePostDescription;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DescriptionDTO {
    private TradePostDescription.contentType type;

    private String value;

    private int sequence;

    private TradePostDescription.TextStyle textStyle;

    private TradePostDescription.TextAlignment textAlignment;

    private String fontSize;
}
