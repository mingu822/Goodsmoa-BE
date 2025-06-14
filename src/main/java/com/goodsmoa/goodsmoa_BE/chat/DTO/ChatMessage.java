package com.goodsmoa.goodsmoa_BE.chat.DTO;

import com.goodsmoa.goodsmoa_BE.chat.Entity.ChatMessageEntity;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private Long id;

    private MessageType type;  // 메시지 타입 (ENTER, TALK, LEAVE)
//
    private Long chatRoomId; // 채팅방 ID

    private String senderId; // 보낸 사람

    private String senderImage;

    private String senderNickname;

    private String content; // 메시지 내용

    private LocalDateTime sendAt;

    private Boolean isRead;


    public enum MessageType {
        ENTER, CHAT, LEAVE ;
    }

    public static ChatMessage fromEntity(ChatMessageEntity entity) {
        return ChatMessage.builder()
                .id(entity.getId())
                .chatRoomId(entity.getChatRoomEntity().getId())
                .senderId(entity.getSenderId().getId()) // UserEntity의 ID를 가져옴
                .senderImage(entity.getSenderId().getImage())
                .senderNickname(entity.getSenderId().getNickname())
                .content(entity.getContent())
                .sendAt(entity.getSendAt())
                .isRead(entity.getIsRead())
                .build();
    }
}
