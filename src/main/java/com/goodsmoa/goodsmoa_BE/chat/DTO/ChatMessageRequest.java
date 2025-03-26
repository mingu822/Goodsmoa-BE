package com.goodsmoa.goodsmoa_BE.chat.DTO;


import com.goodsmoa.goodsmoa_BE.chat.Entity.ChatMessageEntity;
import com.goodsmoa.goodsmoa_BE.chat.Entity.ChatRoomEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatMessageRequest {

    //채팅방 만들 유저 아이디
    @NotNull
    private String userId;
    // 메세지 내용
    @NotBlank(message = "메세지를 입력해주세요")
    private String content;

    public ChatMessageEntity toEntity(ChatRoomEntity chatRoomEntity, UserEntity user) {
        return ChatMessageEntity.builder()
                .content(content)
                .chatRoomId(chatRoomEntity)
                .user(user)
                .build();
    }
}
