package com.goodsmoa.goodsmoa_BE.chat.DTO;

import lombok.Builder;
import lombok.Getter;
import com.goodsmoa.goodsmoa_BE.chat.Entity.ChatJoinEntity;
import com.goodsmoa.goodsmoa_BE.chat.Entity.ChatRoomEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import jakarta.validation.constraints.NotNull;



@Getter
@Builder
public class ChatJoinRequest {

    // 참여할 채팅방 아이디
    @NotNull(message = "채팅방 ID는 필수입니다.")
    private Long chatRoomId;

    // 참여한 유저의 아이디
    @NotNull(message = "사용자 ID는 필수입니다.")
    private String userId;

    @NotNull(message = "채팅 참여 상태는 필수입니다.")
    private Boolean status; // 사용자가 채팅방을 나갔는지 여부

    public ChatJoinEntity toEntity(ChatRoomEntity chatRoom, UserEntity user) {
        return ChatJoinEntity.builder()
                .chatRoomEntity(chatRoom)
                .user(user)
                .status(status)
                .build();
    }
}
