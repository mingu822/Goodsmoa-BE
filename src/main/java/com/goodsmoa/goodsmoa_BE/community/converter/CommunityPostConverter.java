package com.goodsmoa.goodsmoa_BE.community.converter;

import com.goodsmoa.goodsmoa_BE.category.Entity.Category;
import com.goodsmoa.goodsmoa_BE.community.dto.CommunityPostRequest;
import com.goodsmoa.goodsmoa_BE.community.dto.CommunityPostResponse;
import com.goodsmoa.goodsmoa_BE.community.dto.CommunityReplyResponse;
import com.goodsmoa.goodsmoa_BE.community.entity.CommunityPostEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class CommunityPostConverter {

    public CommunityPostEntity toEntity(UserEntity user, Category category, CommunityPostRequest req) {
        return CommunityPostEntity.builder()
                .title(req.getTitle())
                .content(req.getContent())
                .detailCategory(req.getDetailCategory())
                .category(category)
                .user(user)
                .views(0L)
                .createdAt(java.time.LocalDateTime.now())
                .build();
    }



    public CommunityPostResponse toResponseDto(CommunityPostEntity entity, Long replyCount) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        return CommunityPostResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .detailCategory(entity.getDetailCategory())
                .categoryName(entity.getCategory().getName())
                .nickname(entity.getUser().getNickname())
                .views(entity.getViews())
                .createdAt(entity.getCreatedAt().format(formatter))
                .updatedAt(entity.getUpdatedAt() == null ? null : entity.getUpdatedAt().format(formatter))
                .replyCount(replyCount) // 댓글 수 반영
                .build();
    }


    //댓글 포함해 응답하는 ver 오버로딩
    public CommunityPostResponse toResponseDto(CommunityPostEntity entity, Long replyCount, List<CommunityReplyResponse> replies) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        return CommunityPostResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .detailCategory(entity.getDetailCategory())
                .categoryName(entity.getCategory().getName())
                .nickname(entity.getUser().getNickname())
                .views(entity.getViews())
                .createdAt(entity.getCreatedAt().format(formatter))
                .updatedAt(entity.getUpdatedAt() == null ? null : entity.getUpdatedAt().format(formatter))
                .replyCount(replyCount)
                .replies(replies)
                .build();
    }



}
