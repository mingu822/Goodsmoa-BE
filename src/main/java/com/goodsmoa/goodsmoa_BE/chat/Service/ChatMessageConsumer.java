package com.goodsmoa.goodsmoa_BE.chat.Service;

import com.goodsmoa.goodsmoa_BE.chat.DTO.ChatMessage;
import com.goodsmoa.goodsmoa_BE.chat.Entity.ChatMessageEntity;
import com.goodsmoa.goodsmoa_BE.chat.Repository.ChatMessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import com.goodsmoa.goodsmoa_BE.user.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageConsumer {

    private final ChatMessageRepository chatMessageRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository; // 🔥 UserEntity 조회를 위해 추가

    @RabbitListener(queues = "#{'${rabbitmq.queue}'}")
    public void receiveMessage(String message) {
        try {
            ChatMessage chatMessage = objectMapper.readValue(message, ChatMessage.class);
            log.info("🐰 MQ에서 메시지 수신: {}", chatMessage);

            // 🔥 senderId로 UserEntity 조회
            String senderId = chatMessage.getSenderId();
            UserEntity sender = userRepository.findById(senderId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자 ID: " + senderId));

            // DB 저장
            ChatMessageEntity entity = ChatMessageEntity.builder()
                    .id(chatMessage.getChatRoomId())
                    .senderId(sender) // 🔥 UserEntity 전달
                    .content(chatMessage.getContent())
                    .sendAt(chatMessage.getSendAt())
                    .build();
            chatMessageRepository.save(entity);

            // 구독자에게 브로드캐스트
            String destination = "/sub/chat/" + chatMessage.getChatRoomId();
            messagingTemplate.convertAndSend(destination, chatMessage);

        } catch (Exception e) {
            log.error("❌ MQ 메시지 처리 오류", e);
        }
    }
}