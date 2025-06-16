package com.goodsmoa.goodsmoa_BE.search.converter;

import com.goodsmoa.goodsmoa_BE.community.entity.CommunityPostEntity;
import com.goodsmoa.goodsmoa_BE.enums.Board;
import com.goodsmoa.goodsmoa_BE.search.document.SearchDocument;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CommunityDocumentConverter implements DocumentConverter<CommunityPostEntity>{
    @Override
    public SearchDocument convert(CommunityPostEntity entity) {
        return SearchDocument.builder()
                .id(Board.COMMUNITY.name()+"_"+entity.getId())
                .userId(entity.getUser().getId())
                .views(entity.getViews())
                .title(entity.getTitle())
                .description(entity.getContent())
                .boardType(Board.COMMUNITY)
                .categoryId(entity.getCategory().getId())
                .pulledAt(LocalDateTime.now())
                .build();
    }
}
