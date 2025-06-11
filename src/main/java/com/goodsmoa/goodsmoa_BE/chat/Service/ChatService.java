package com.goodsmoa.goodsmoa_BE.chat.Service;

import com.goodsmoa.goodsmoa_BE.chat.DTO.ChatMessage;
import com.goodsmoa.goodsmoa_BE.chat.Entity.ChatMessageEntity;
import com.goodsmoa.goodsmoa_BE.chat.Entity.ChatRoomEntity;
import com.goodsmoa.goodsmoa_BE.chat.Repository.ChatMessageRepository;
import com.goodsmoa.goodsmoa_BE.chat.Repository.ChatRoomRepository;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import com.goodsmoa.goodsmoa_BE.user.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate; // ✅ RabbitTemplate 추가
import org.springframework.beans.factory.annotation.Value;   // ✅ Value 추가 (routingKey, exchangeName)
import org.springframework.messaging.simp.SimpMessagingTemplate; // 읽음 처리 알림은 SimpMessagingTemplate으로 계속 보냄
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate; // WebSocket으로 읽음 처리 알림 보낼 때 사용

    private final RabbitTemplate rabbitTemplate; // ✅ RabbitMQ로 메시지 발행을 위한 RabbitTemplate
    @Value("${rabbitmq.exchange}")
    private String exchangeName;
    @Value("${rabbitmq.routingKey}")
    private String routingKey;


    // ✅ sendMessage: 클라이언트로부터 메시지를 받아 MQ로 발행
    public void publishChatMessage(ChatMessage chatMessage) {
        // 메시지 전송 시간, isRead 등 초기화 (컨슈머에서 할 수도 있지만, 여기서 미리 설정해두는 것도 좋음)
        // ID는 DB에 저장될 때 자동 생성되므로 여기서는 설정하지 않습니다.
        chatMessage.setSendAt(LocalDateTime.now());
        chatMessage.setIsRead(false);

        // RabbitMQ로 메시지 발행
        rabbitTemplate.convertAndSend(exchangeName, routingKey, chatMessage);
        log.info("✅ 메시지를 RabbitMQ로 발행했습니다: {}", chatMessage);
    }

    // 기존 saveChatMessage는 ChatMessageConsumer로 로직이 이동하므로, 이 서비스에서는 더 이상 직접 DB 저장을 하지 않습니다.
    // 만약 기존 saveChatMessage를 다른 곳에서 사용하고 있다면, 해당 호출 부분을 publishChatMessage로 변경해야 합니다.
    // 또는 ChatMessageConsumer에서 메시지 저장이 완료된 후의 로직이 필요하다면 다른 메서드를 만들어야 합니다.

    public String enterChatRoom(Long chatRoomId) {
        if (!chatRoomRepository.existsById(chatRoomId)) {
            throw new IllegalArgumentException("채팅방이 존재하지 않습니다");
        }
        return "채팅방 [" + chatRoomId + "]에 입장하였습니다.";
    }

    @Transactional
    public void markAsRead(Long chatRoomId, UserEntity currentUser) {
        String userId = currentUser.getId();
        List<ChatMessageEntity> unreadMessages =
                chatMessageRepository.findByChatRoomEntity_IdAndSenderId_IdNotAndIsReadFalse(chatRoomId, userId);

        if (unreadMessages.isEmpty()) return;

        List<Long> readMessageIds = unreadMessages.stream()
                .map(ChatMessageEntity::getId)
                .collect(Collectors.toList());

        chatMessageRepository.markAsReadByChatRoomIdAndUserId(chatRoomId, userId);

        messagingTemplate.convertAndSend(
                "/queue/chat/" + chatRoomId + "/read",
                readMessageIds
        );
    }

    public List<ChatMessage> getMessagesByRoomId(Long roomId) {
        List<ChatMessageEntity> messageEntities =
                chatMessageRepository.findByChatRoomEntity_IdOrderBySendAtAsc(roomId);

        return messageEntities.stream()
                .map(ChatMessage::fromEntity)
                .collect(Collectors.toList());
    }
}