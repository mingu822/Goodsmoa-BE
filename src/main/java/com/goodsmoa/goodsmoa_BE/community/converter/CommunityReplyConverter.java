package com.goodsmoa.goodsmoa_BE.community.converter;

import com.goodsmoa.goodsmoa_BE.community.dto.CommunityReplyResponse;
import com.goodsmoa.goodsmoa_BE.community.entity.CommunityReplyEntity;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@Component
public class CommunityReplyConverter {

    // entity -> Response DTO
    public CommunityReplyResponse toDto(CommunityReplyEntity entity) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        return CommunityReplyResponse.builder()
                .id(entity.getId())
                .postId(entity.getPost().getId())
                .parentId(entity.getParentReply() == null ? null : entity.getParentReply().getId())
                .content(entity.getContent())
                .children(new ArrayList<>())
                .nickname(entity.getUser().getNickname())
                .createdAt(entity.getCreatedAt().format(formatter))
                .updatedAt(entity.getUpdatedAt() == null ? null : entity.getUpdatedAt().format(formatter))
                .build();
    }







}
