package com.goodsmoa.goodsmoa_BE.search.converter;

import com.goodsmoa.goodsmoa_BE.enums.Board;
import com.goodsmoa.goodsmoa_BE.search.document.SearchDocument;
import com.goodsmoa.goodsmoa_BE.trade.Entity.TradePostEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Component
public class TradeDocumentConverter implements DocumentConverter<TradePostEntity>{
    @Override
    public SearchDocument convert(TradePostEntity entity) {
        String description = "";
        String htmlContent = entity.getContent();
        // ✅ content 필드가 null이 아닌 경우, HTML 태그를 모두 제거한다.
        if (htmlContent != null && !htmlContent.isEmpty()) {
            // 정규표현식을 사용하여 모든 HTML 태그(<...>)를 빈 문자열로 대체
            description = htmlContent.replaceAll("<[^>]*>", "");
        }
        return SearchDocument.builder()
                .id(Board.TRADE.name() + "_" + entity.getId())
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
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
