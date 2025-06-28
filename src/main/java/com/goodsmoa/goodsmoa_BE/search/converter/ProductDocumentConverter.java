package com.goodsmoa.goodsmoa_BE.search.converter;

import com.goodsmoa.goodsmoa_BE.enums.Board;
import com.goodsmoa.goodsmoa_BE.product.entity.ProductPostEntity;
import com.goodsmoa.goodsmoa_BE.search.document.SearchDocument;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ProductDocumentConverter implements DocumentConverter<ProductPostEntity>{
    @Override
    public SearchDocument convert(ProductPostEntity entity) {
        return SearchDocument.builder()
                .id(Board.PRODUCT.name()+"_"+entity.getId())
                .userId(entity.getUser().getId())
                .thumbnailUrl(entity.getThumbnailImage())
                .views(entity.getViews())
                .title(entity.getTitle())
                .likes(entity.getLikes() )
                .description(entity.getContent())
                .hashtag(entity.getHashtag())
                .boardType(Board.PRODUCT)
                .categoryId(entity.getCategory().getId())
                .startTime(entity.getStartTime().atStartOfDay())
                .endTime(entity.getEndTime().atStartOfDay())
                .pulledAt(LocalDateTime.now())
                .build();
    }
}
