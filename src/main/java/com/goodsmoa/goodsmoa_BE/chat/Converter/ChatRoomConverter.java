package com.goodsmoa.goodsmoa_BE.chat.Converter;

import com.goodsmoa.goodsmoa_BE.chat.DTO.ChatRoom;
import com.goodsmoa.goodsmoa_BE.chat.DTO.ChatRoomResponse;
import com.goodsmoa.goodsmoa_BE.chat.Entity.ChatRoomEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class ChatRoomConverter {
//
    public static ChatRoomEntity toEntity(ChatRoom dto, UserEntity buyer, UserEntity seller) {
        return ChatRoomEntity.builder()
                .postId(dto.getId())
                .seller(seller)
                .buyer(buyer)
                .build();
    }

    public static ChatRoomResponse toResponse(ChatRoomEntity entity) {
        return ChatRoomResponse.builder()
                .id(entity.getId())
                .buyer(entity.getBuyer().getId())
                .seller(entity.getSeller().getId())
                .build();
    }
}
