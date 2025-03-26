package com.goodsmoa.goodsmoa_BE.chat.DTO;

import com.goodsmoa.goodsmoa_BE.chat.Entity.ChatRoomEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatRoomRequest {

    // 채팅방 만드는 사람아이디
    @NotBlank
    private String user;

    // 채팅방 제목 설정하는 거
    @NotBlank(message = "채팅방 제목은 필수입니다.")
    @Size(max = 15, message = "채팅방 제목은 최대 15자까지 가능합니다.")
    private String title;

    // 채팅방 활성화 비활성화 확인
    @NotNull(message = "채팅방 상태는 필수입니다.")
    private  Boolean status;

    public ChatRoomEntity toEntity(ChatRoomRequest chatRoomRequest, UserEntity user) {
        return ChatRoomEntity.builder()
                .title(title)
                .status(status)
                .user(user)
                .build();
    }
}
