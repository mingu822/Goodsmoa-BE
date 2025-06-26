package com.goodsmoa.goodsmoa_BE.search.converter;

import com.goodsmoa.goodsmoa_BE.commission.entity.CommissionPostEntity;
import com.goodsmoa.goodsmoa_BE.enums.Board;
import com.goodsmoa.goodsmoa_BE.product.entity.ProductPostEntity;
import com.goodsmoa.goodsmoa_BE.search.document.SearchDocument;
import com.goodsmoa.goodsmoa_BE.trade.Entity.TradePostEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Component
public class CommissionDocumentConverter implements DocumentConverter<CommissionPostEntity>{
    @Override
    public SearchDocument convert(CommissionPostEntity entity) {
        return SearchDocument.builder()
                .id(Board.COMMISSION.name()+"_"+entity.getId())
                .userId(entity.getUser().getId())
                .thumbnailUrl(entity.getThumbnailImage())
                .views(entity.getViews())
                .title(entity.getTitle())
                .description(entity.getContent())
                .hashtag(entity.getHashtag())
                .boardType(Board.COMMISSION)
                .pulledAt(LocalDateTime.now())
                .build();
    }
}
