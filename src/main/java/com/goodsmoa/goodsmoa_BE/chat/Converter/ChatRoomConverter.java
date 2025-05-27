package com.goodsmoa.goodsmoa_BE.chat.Converter;

import com.goodsmoa.goodsmoa_BE.chat.DTO.ChatRoom;
import com.goodsmoa.goodsmoa_BE.chat.DTO.ChatRoomResponse;
import com.goodsmoa.goodsmoa_BE.chat.Entity.ChatRoomEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class ChatRoomConverter {

    public static ChatRoomEntity toEntity(ChatRoom dto, UserEntity sender, UserEntity receiver) {
        return ChatRoomEntity.builder()
                .title(dto.getTitle())
                .status(dto.getStatus() != null ? dto.getStatus() : true) // 기본값 true
                .sender(sender)
                .receiver(receiver)
                .build();
    }

    public static ChatRoomResponse toResponse(ChatRoomEntity entity) {
        return ChatRoomResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .build();
    }
}
