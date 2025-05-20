package com.goodsmoa.goodsmoa_BE.community.converter;

import com.goodsmoa.goodsmoa_BE.community.dto.CommunityPostSimpleResponse;
import com.goodsmoa.goodsmoa_BE.community.entity.CommunityPostEntity;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class CommunitySimplePostConverter {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // 목록용 간단 응답 DTO로 변환
    public CommunityPostSimpleResponse toDto(CommunityPostEntity entity, Long replyCount, Long likeCount) {
        return CommunityPostSimpleResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .detailCategory(entity.getDetailCategory())
                .categoryName(entity.getCategory().getName())
                .nickname(entity.getUser().getNickname())
                .views(entity.getViews())
                .createdAt(entity.getCreatedAt().format(formatter))
                .updatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt().format(formatter) : null)
                .replyCount(replyCount)
                .likeCount(likeCount)
                .build();
    }
}
