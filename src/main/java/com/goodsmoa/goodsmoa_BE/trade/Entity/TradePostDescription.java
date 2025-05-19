package com.goodsmoa.goodsmoa_BE.trade.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TradePostDescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    private contentType contentType;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String value;

    private int sequence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trade_post_id")
    private TradePostEntity tradePost;

    @Enumerated(EnumType.STRING)
    @Column(name = "text_style")
    private TextStyle testStyle;

    @Enumerated(EnumType.STRING)
    @Column(name = "alignment")
    private TextAlignment textAlignment;

    private String fontSize;

    public enum TextAlignment{
        LEFT, CENTER, RIGHT
    }

    public enum TextStyle {
        NORMAL,    // 기본값
        BOLD, //굵음
        ITALIC, // 기울기
        UNDERLINE, // 밑줄
        BOLD_ITALIC,
        BOLD_UNDERLINE,
        ITALIC_UNDERLINE,
        BOLD_ITALIC_UNDERLINE
    }

    public enum contentType{
        TEXT, IMAGE
    }
}
