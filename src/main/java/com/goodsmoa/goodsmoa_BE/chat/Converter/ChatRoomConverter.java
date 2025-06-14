package com.goodsmoa.goodsmoa_BE.chat.Converter;

import com.goodsmoa.goodsmoa_BE.chat.DTO.ChatRoom;
import com.goodsmoa.goodsmoa_BE.chat.DTO.ChatRoomResponse;
import com.goodsmoa.goodsmoa_BE.chat.Entity.ChatMessageEntity;
import com.goodsmoa.goodsmoa_BE.chat.Entity.ChatRoomEntity;
import com.goodsmoa.goodsmoa_BE.chat.Repository.ChatMessageRepository;
import com.goodsmoa.goodsmoa_BE.user.DTO.UserInfo;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import com.goodsmoa.goodsmoa_BE.user.Converter.UserInfoConverter;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatRoomConverter {
    private final UserInfoConverter userInfoConverter; // UserInfo 변환을 위해 주입

    private final ChatMessageRepository chatMessageRepository;
    public static ChatRoomEntity toEntity(ChatRoom dto, UserEntity buyer, UserEntity seller) {
        return ChatRoomEntity.builder()
                .postId(dto.getId())
                .seller(seller)
                .buyer(buyer)
                .build();
    }

    public static ChatRoomResponse toResponse(ChatRoomEntity entity) {
        return ChatRoomResponse.builder()
                .id(entity.getId())
                .buyerId(entity.getBuyer().getId())
                .sellerId(entity.getSeller().getId())
                .build();
    }



    public ChatRoomResponse toChatRoomResponse(ChatRoomEntity chatRoomEntity, UserEntity currentUser) {
        if (chatRoomEntity == null) {
            return null;
        }

        UserInfo buyerInfo = userInfoConverter.toUserInfo(chatRoomEntity.getBuyer());
        UserInfo sellerInfo = userInfoConverter.toUserInfo(chatRoomEntity.getSeller());
        List<ChatMessageEntity> messages = chatMessageRepository.findByChatRoomEntity(chatRoomEntity);
        ChatMessageEntity lastMessages = messages.stream()
                .max(Comparator.comparing(ChatMessageEntity::getSendAt))
                .orElse(null);
        ChatMessageEntity lastMessage = chatMessageRepository.findTopByChatRoomEntityOrderBySendAtDesc(chatRoomEntity)
                .orElse(null);
        String lastMessageContent = lastMessage != null ? lastMessage.getContent() : "";
        LocalDateTime lastMessageTime = lastMessage != null ? lastMessage.getSendAt() : null;
        String lastMessageReceiverId = lastMessage != null && lastMessage.getReceiverId() != null
                ? lastMessage.getReceiverId().getId()
                : null;


        // 읽지 않은 메시지 개수
        int unreadCount = chatMessageRepository.countByChatRoomEntityAndReceiverId_IdAndIsReadFalse(
                chatRoomEntity, currentUser.getId()
        );

        return ChatRoomResponse.builder()
                .id(chatRoomEntity.getId())
                .buyer(buyerInfo)
                .seller(sellerInfo)
                .sellerNickname(sellerInfo.getNickname())
                .sellerProfileImage(sellerInfo.getImage())
                .lastMessageContent(lastMessageContent)
                .lastMessageTime(lastMessageTime)
                .lastMessageReceiverId(lastMessageReceiverId)
                .unreadCount(unreadCount)
                .build();
    }
}
