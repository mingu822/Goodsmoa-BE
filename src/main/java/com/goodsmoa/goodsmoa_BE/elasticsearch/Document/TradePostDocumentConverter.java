package com.goodsmoa.goodsmoa_BE.elasticsearch.Document;

import com.goodsmoa.goodsmoa_BE.trade.Entity.TradePostEntity;
import org.springframework.stereotype.Component;

@Component
public class TradePostDocumentConverter {

    public TradePostDocument toSearchDocument(TradePostEntity entity) {
        return TradePostDocument.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .hashtag(entity.getHashtag())
                .build();
    }
}
