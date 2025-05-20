package com.goodsmoa.goodsmoa_BE.community.converter;

import com.goodsmoa.goodsmoa_BE.community.dto.MyReplySimpleResponse;
import com.goodsmoa.goodsmoa_BE.community.entity.CommunityReplyEntity;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class MyReplySimpleConverter {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public MyReplySimpleResponse toDto(CommunityReplyEntity entity) {
        return MyReplySimpleResponse.builder()
                .id(entity.getId())
                .postId(entity.getPost().getId())
                .content(entity.getContent())
                .nickname(entity.getUser().getNickname())
                .createdAt(entity.getCreatedAt().format(formatter))
                .updatedAt(entity.getUpdatedAt() == null ? null : entity.getUpdatedAt().format(formatter))
                .build();
    }
}
