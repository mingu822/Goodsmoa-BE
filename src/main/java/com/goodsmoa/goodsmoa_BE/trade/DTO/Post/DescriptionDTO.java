package com.goodsmoa.goodsmoa_BE.trade.DTO.Post;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.goodsmoa.goodsmoa_BE.trade.Entity.TradePostDescription;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)  //
public class DescriptionDTO {
    private TradePostDescription.contentType type;

    private String value;

    private int sequence;

    private TradePostDescription.TextStyle textStyle;

    private TradePostDescription.TextAlignment textAlignment;

    private String fontSize;

    @JsonIgnore
    public boolean isText(){
        return "TEXT".equalsIgnoreCase(String.valueOf(type));
    }

    public void sanitize() {
        if (isText()) {
            if (this.textStyle == null) this.textStyle = TradePostDescription.TextStyle.NORMAL;
            if (this.textAlignment == null) this.textAlignment = TradePostDescription.TextAlignment.LEFT;
        } else {
            this.textStyle = null;
            this.textAlignment = null;
            this.fontSize = null;
        }
    }
}
