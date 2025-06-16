package com.goodsmoa.goodsmoa_BE.search.converter;

import com.goodsmoa.goodsmoa_BE.enums.Board;
import com.goodsmoa.goodsmoa_BE.search.document.SearchDocument;
import com.goodsmoa.goodsmoa_BE.trade.Entity.TradePostDescription;
import com.goodsmoa.goodsmoa_BE.trade.Entity.TradePostEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Component
public class TradeDocumentConverter implements DocumentConverter<TradePostEntity>{
    @Override
    public SearchDocument convert(TradePostEntity entity) {
        String description = "";
        if (entity.getContentDescriptions() != null && !entity.getContentDescriptions().isEmpty()) {
            description = entity.getContentDescriptions().stream()
                    .filter(desc -> desc.getContentType() == TradePostDescription.contentType.TEXT)
                    .map(TradePostDescription::getValue)
                    .collect(Collectors.joining("\n"));
        }
        return SearchDocument.builder()
                .id(Board.TRADE.name() + "_" + entity.getId())
                .userId(entity.getUser().getId())
                .thumbnailUrl(entity.getThumbnailImage())
                .views(entity.getViews())
                .title(entity.getTitle())
                .description(description)
                .hashtag(entity.getHashtag())
                .boardType(Board.TRADE)
                .categoryId(entity.getCategory().getId())
                .pulledAt(entity.getPulledAt() != null ? entity.getPulledAt() : LocalDateTime.now())
                .build();
    }
}
