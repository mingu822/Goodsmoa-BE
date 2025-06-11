package com.goodsmoa.goodsmoa_BE.chat.DTO; // 실제 프로젝트 경로에 맞게 수정하세요.

import com.goodsmoa.goodsmoa_BE.chat.Entity.ChatMessageEntity;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 서버가 클라이언트에게 채팅 메시지 정보를 보낼 때 사용하는 DTO
 * DB에 저장된 완전한 메시지 정보를 담고 있습니다.
 */
@Getter
public class ChatMessageResponse {

    private final Long id;      // DB에 생성된 메시지 ID
    private final Long chatRoomId;     // 메시지가 속한 채팅방 ID
    private final String senderId;         // 메시지를 보낸 사람의 ID
    private final String content;        // 메시지 내용
    private final LocalDateTime sendAt;  // 메시지 발송 시간
    private final boolean isRead;        // 메시지 읽음 여부

    /**
     * ChatMessageEntity 객체를 ChatMessageResponse DTO로 변환하는 생성자
     * @param entity DB에서 조회한 ChatMessageEntity 원본 객체
     */
    public ChatMessageResponse(ChatMessageEntity entity) {
        this.id = entity.getId();
        this.chatRoomId = entity.getChatRoomEntity().getId();

        // ChatMessageEntity의 senderId 필드(UserEntity 타입)에서 id와 nickname을 가져옵니다.
        this.senderId = entity.getSenderId().getId();
        this.content = entity.getContent();
        this.sendAt = entity.getSendAt();
        this.isRead = entity.getIsRead();
    }
}