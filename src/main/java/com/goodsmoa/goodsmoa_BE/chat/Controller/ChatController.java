package com.goodsmoa.goodsmoa_BE.chat.Controller;

import com.goodsmoa.goodsmoa_BE.chat.DTO.ChatMessage;
import com.goodsmoa.goodsmoa_BE.chat.DTO.ReadMessageRequest;
import com.goodsmoa.goodsmoa_BE.chat.Service.ChatService;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.security.core.annotation.AuthenticationPrincipal;


import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate; // 읽음 처리 알림은 계속 사용

    /**
     * 클라이언트로부터 메시지를 수신하고 RabbitMQ로 발행합니다.
     * 실제 DB 저장 및 STOMP 브로드캐스트는 컨슈머가 처리합니다.
     */
    @MessageMapping("/chat")
    public void sendMessage(@Payload ChatMessage chatMessage, Principal principal) {
        try {
            log.info("📥 메시지 수신 (STOMP): {}", chatMessage);
            // Principal을 통해 인증된 사용자 정보 확인
            if (principal != null) {
                if (principal instanceof UsernamePasswordAuthenticationToken token) {
                    Object rawPrincipal = token.getPrincipal();
                    if (rawPrincipal instanceof String userIdString) {
                        chatMessage.setSenderId(userIdString);
                        log.info("✅ 인증된 사용자 ID (String principal): {}", userIdString);
                    } else if (rawPrincipal instanceof UserEntity userEntity) {
                        String userIdFromEntity = userEntity.getId();
                        chatMessage.setSenderId(userIdFromEntity);
                        log.info("✅ 인증된 사용자 ID (UserEntity principal): {}", userIdFromEntity);
                    } else {
                        log.warn("⚠️ 예상 외 principal 타입이며 처리 불가: {}", rawPrincipal.getClass());
                    }
                } else {
                    log.warn("⚠️ Principal이 UsernamePasswordAuthenticationToken 타입이 아님: {}", principal.getClass());
                }
            } else {
                log.warn("⚠️ principal이 null입니다");
            }

            // 메시지를 직접 저장하는 대신, RabbitMQ로 발행합니다.
            chatService.publishChatMessage(chatMessage);
            log.info("📤 메시지를 RabbitMQ로 발행하도록 요청 완료.");

        } catch (Exception e) {
            log.error("❌ 메시지 처리 중 오류 발생", e);
        }
    }

    // 기존의 읽음 처리 및 메시지 조회 API는 그대로 유지합니다.
    @PostMapping("/chat/read")
    public ResponseEntity<Void> readMessage(
            @RequestBody ReadMessageRequest req,
            @AuthenticationPrincipal UserEntity user
    ) {
        chatService.markAsRead(req.getChatRoomId(), user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/chatroom/room/{roomId}/messages")
    public ResponseEntity<List<ChatMessage>> getRoomMessages(@PathVariable Long roomId) {
        List<ChatMessage> messages = chatService.getMessagesByRoomId(roomId);
        return ResponseEntity.ok(messages);
    }
}