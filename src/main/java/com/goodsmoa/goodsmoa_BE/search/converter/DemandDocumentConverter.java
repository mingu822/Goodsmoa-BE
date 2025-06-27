package com.goodsmoa.goodsmoa_BE.search.converter;

import com.goodsmoa.goodsmoa_BE.demand.entity.DemandPostEntity;
import com.goodsmoa.goodsmoa_BE.demand.service.DemandLikeService;
import com.goodsmoa.goodsmoa_BE.enums.Board;
import com.goodsmoa.goodsmoa_BE.search.document.SearchDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DemandDocumentConverter implements DocumentConverter<DemandPostEntity>{

    @Override
    public SearchDocument convert(DemandPostEntity entity) {
        return SearchDocument.builder()
                .id(Board.DEMAND.name()+"_"+entity.getId())
                .userId(entity.getUser().getId())
                .thumbnailUrl(entity.getImageUrl())
                .views(entity.getViews())
                .likes(entity.getLikes())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .hashtag(entity.getHashtag())
                .boardType(Board.DEMAND)
                .categoryId(entity.getCategory().getId())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .pulledAt(entity.getPulledAt())
                .build();
    }
}
