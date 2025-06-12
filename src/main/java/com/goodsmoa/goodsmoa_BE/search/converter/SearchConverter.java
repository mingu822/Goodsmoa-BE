package com.goodsmoa.goodsmoa_BE.search.converter;

import com.goodsmoa.goodsmoa_BE.enums.Board;
import com.goodsmoa.goodsmoa_BE.search.dto.SearchDocWithUserResponse;
import com.goodsmoa.goodsmoa_BE.search.document.SearchDocument;
import com.goodsmoa.goodsmoa_BE.search.entity.SearchEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class SearchConverter {
    public SearchDocument toDocument(SearchEntity entity, Board board){
        return SearchDocument.builder()
                .id(board.name()+"_"+entity.getId())
                .userId(entity.getUser().getId())
                .thumbnailUrl(entity.getImageUrl())
                .views(entity.getViews())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .hashtag(entity.getHashtag())
                .boardType(board)
                .categoryId(entity.getCategory().getId())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .pulledAt(LocalDateTime.now())
                .build();
    }

    public SearchDocWithUserResponse toSearchPostWithUserResponse(SearchDocument doc, UserEntity user){
        return SearchDocWithUserResponse.builder()
                .id(doc.getId())
                .boardType(doc.getBoardType().getName())
                .title(doc.getTitle())
                .hashtag(doc.getHashtag())
                .thumbnailUrl(doc.getThumbnailUrl())
                .views(doc.getViews())
                .endTime(doc.getEndTime())
                .nickname(user.getNickname())
                .profileUrl(user.getImage())
                .build();
    }
}
