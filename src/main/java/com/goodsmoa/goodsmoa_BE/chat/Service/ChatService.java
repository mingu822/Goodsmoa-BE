package com.goodsmoa.goodsmoa_BE.chat.Service;

import com.goodsmoa.goodsmoa_BE.chat.DTO.ChatMessage;
import com.goodsmoa.goodsmoa_BE.chat.Entity.ChatMessageEntity;
import com.goodsmoa.goodsmoa_BE.chat.Entity.ChatRoomEntity;
import com.goodsmoa.goodsmoa_BE.chat.Repository.ChatMessageRepository;
import com.goodsmoa.goodsmoa_BE.chat.Repository.ChatRoomRepository;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import com.goodsmoa.goodsmoa_BE.user.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    @Transactional
    public ChatMessage saveChatMessage(ChatMessage chatMessage) {
        ChatRoomEntity chatRoom = chatRoomRepository.findById(chatMessage.getChatRoomId())
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다"));

        UserEntity sender = userRepository.findById(chatMessage.getSenderId())
                .orElseThrow(() -> new IllegalArgumentException("보낸 유저가 존재하지 않습니다"));

        ChatMessageEntity chatMessageEntity = ChatMessageEntity.builder()
                .chatRoomEntity(chatRoom)
                .senderId(sender)
                .content(chatMessage.getContent())
                .type(chatMessage.getType())
                .sendAt(LocalDateTime.now())
                .build();
        chatMessageRepository.save(chatMessageEntity);
        chatMessage.setSendAt(chatMessageEntity.getSendAt());
        return chatMessage;
    }

    public String enterChatRoom(Long chatRoomId) {
        if (!chatRoomRepository.existsById(chatRoomId)) {
            throw new IllegalArgumentException("채팅방이 존재하지 않습니다");
        }
        return "채팅방 [" + chatRoomId + "]에 입장하였습니다.";
    }
}
