package com.goodsmoa.goodsmoa_BE.chat.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private MessageType type;  // 메시지 타입 (ENTER, TALK, LEAVE)

    private Long chatRoomId; // 채팅방 ID

    private String senderId; // 보낸 사람

    private String content; // 메시지 내용

    private LocalDateTime sendAt;

    public enum MessageType {
        ENTER, CHAT, LEAVE ;
    }
}
