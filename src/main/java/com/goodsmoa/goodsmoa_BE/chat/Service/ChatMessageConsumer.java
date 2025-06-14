package com.goodsmoa.goodsmoa_BE.chat.Service; // 서비스 패키지에 두는 것이 적절합니다.

import com.goodsmoa.goodsmoa_BE.chat.DTO.ChatMessage;
import com.goodsmoa.goodsmoa_BE.chat.Entity.ChatMessageEntity;
import com.goodsmoa.goodsmoa_BE.chat.Repository.ChatMessageRepository;
import com.goodsmoa.goodsmoa_BE.chat.Repository.ChatRoomRepository; // ✅ ChatRoomRepository 추가
import com.goodsmoa.goodsmoa_BE.chat.Entity.ChatRoomEntity;       // ✅ ChatRoomEntity 추가
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import com.goodsmoa.goodsmoa_BE.user.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // ✅ 트랜잭션 추가
import java.time.LocalDateTime;
import java.util.Map;

@Service // 서비스 계층으로 유지
@RequiredArgsConstructor
@Slf4j
public class ChatMessageConsumer {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository; // ✅ 추가
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    // 🔥 MessageConverter를 RabbitMQConfig에 설정했다면, String message 대신 ChatMessage 객체를 직접 받을 수 있습니다.
    @RabbitListener(queues = "${rabbitmq.queue}")
    @Transactional // ✅ 메시지 처리 로직에 트랜잭션 적용
    public void receiveMessage(ChatMessage chatMessage) { // ✅ ChatMessage 객체로 직접 받도록 변경
        try {
            log.info("🐰 MQ에서 메시지 수신: {}", chatMessage);

            // 1. senderId로 UserEntity 조회
            String senderId = chatMessage.getSenderId();
            UserEntity sender = userRepository.findById(senderId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자 ID: " + senderId));

            // 2. ChatRoomEntity 조회
            ChatRoomEntity chatRoom = chatRoomRepository.findById(chatMessage.getChatRoomId())
                    .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다. ID: " + chatMessage.getChatRoomId()));
            UserEntity receiver = null;
            if (chatRoom.getBuyer().getId().equals(senderId)) {
                receiver = chatRoom.getSeller();
            } else {
                receiver = chatRoom.getBuyer();
            }
            // 3. DB 저장
            ChatMessageEntity chatMessageEntity = ChatMessageEntity.builder()
                    .chatRoomEntity(chatRoom) // 올바른 ChatRoomEntity 연결
                    .senderId(sender)
                    .receiverId(receiver)
                    .content(chatMessage.getContent())
                    .type(chatMessage.getType())
                    .sendAt(chatMessage.getSendAt() != null ? chatMessage.getSendAt() : LocalDateTime.now()) // 발행 시점 사용, 없으면 현재 시점
                    .isRead(false) // 초기값은 항상 false (컨슈머가 저장할 때)
                    .build();

            ChatMessageEntity savedEntity = chatMessageRepository.save(chatMessageEntity); // ✅ 저장된 엔티티 반환받음

            // 4. ChatMessage DTO에 DB에서 생성된 ID, sendAt, isRead 값 설정
            // 클라이언트로 보낼 DTO에 DB에 저장된 실제 정보(ID, sendAt, isRead)를 업데이트합니다.
            // 이 DTO는 메시지 발행 시점에 생성되어 RabbitMQ를 통해 전달된 것임.
            // 클라이언트에게 브로드캐스팅할 때는 DB에 저장된 최종 상태를 반영해야 함.
            chatMessage.setId(savedEntity.getId());
            chatMessage.setSendAt(savedEntity.getSendAt());
            chatMessage.setIsRead(savedEntity.getIsRead());
            chatMessage.setSenderNickname(sender.getNickname());
            chatMessage.setSenderImage(sender.getImage());
            // 5. 구독자에게 브로드캐스트
            String destination = "/queue/chat/" + chatMessage.getChatRoomId();
            messagingTemplate.convertAndSend(destination, chatMessage); // ✅ ID, isRead가 채워진 DTO 전송

            if (sender != null) {
                String senderDestination = "/queue/user/" + sender.getId() + "/update";
                Map<String, Object> notification = Map.of("type", "NEW_MESSAGE_SENT", "chatRoomId", chatMessage.getChatRoomId());
                messagingTemplate.convertAndSend(senderDestination, notification);
                log.info("✅ 발신자 개인 큐로 알림 전송 완료: {}", senderDestination);
            }

            if (receiver != null) {
                String receiverDestination = "/queue/user/" + receiver.getId() + "/update";
                Map<String, Object> notification = Map.of("type", "NEW_MESSAGE_RECEIVED", "chatRoomId", chatMessage.getChatRoomId());
                messagingTemplate.convertAndSend(receiverDestination, notification);
                log.info("✅ 수신자 개인 큐로 알림 전송 완료: {}", receiverDestination);
            }


        } catch (Exception e) {
            log.error("❌ MQ 메시지 처리 중 오류 발생 (메시지: {})", chatMessage != null ? chatMessage : "null", e);
            // ✅ 여기서는 메시지 처리가 실패했으므로, 재시도 로직이나 DLQ(Dead Letter Queue)로 메시지를 보내는 것을 고려해야 합니다.
            // 현재는 예외 발생 시 메시지가 유실될 수 있습니다.
            throw new AmqpRejectAndDontRequeueException("메시지 처리 실패", e); // RabbitMQ에 재처리하지 말라고 알림
        }
    }
}