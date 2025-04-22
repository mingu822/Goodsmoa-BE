package com.goodsmoa.goodsmoa_BE.chat.Controller;

import com.goodsmoa.goodsmoa_BE.chat.DTO.ChatMessage;
import com.goodsmoa.goodsmoa_BE.chat.Service.ChatService;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 클라이언트로부터 메시지를 수신하고 DB에 저장한 뒤 구독자에게 브로드캐스트합니다.
     * @param chatMessage 클라이언트에서 받은 채팅 메시지
     */
    @MessageMapping("/chat/{chatRoomId}") // 클라이언트는 /pub/chat/message로 전송
    public void sendMessage(@Payload ChatMessage chatMessage, @AuthenticationPrincipal UserEntity user) {
        try {
            log.info("📥 메시지 수신: {}", chatMessage);

            // 메시지 DB 저장
            chatService.saveChatMessage(chatMessage);

            // 메시지를 채팅방 구독자에게 전송
            String destination = "/sub/chat/" + chatMessage.getChatRoomId();
            messagingTemplate.convertAndSend(destination, chatMessage);

            log.info("📤 메시지 전송 완료 → {}", destination);

        } catch (Exception e) {
            log.error("❌ 메시지 처리 중 오류 발생", e);
        }
    }
}
