package com.goodsmoa.goodsmoa_BE.chat.Converter;

import com.goodsmoa.goodsmoa_BE.chat.DTO.ChatRoomRequest;
import com.goodsmoa.goodsmoa_BE.chat.Entity.ChatRoomEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import org.springframework.stereotype.Component;


@Component
public class ChatRoomConverter {


    public static ChatRoomRequest toResponse(ChatRoomEntity entity, UserEntity user) {
        return ChatRoomRequest.builder()
                .title(entity.getTitle())
                .status(entity.getStatus())
                .build();
    }
}
