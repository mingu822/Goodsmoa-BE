package com.goodsmoa.goodsmoa_BE.chat.Repository;

import com.goodsmoa.goodsmoa_BE.chat.DTO.ChatMessage;
import com.goodsmoa.goodsmoa_BE.chat.DTO.ChatRoom;
import com.goodsmoa.goodsmoa_BE.chat.Entity.ChatMessageEntity;
import com.goodsmoa.goodsmoa_BE.chat.Entity.ChatRoomEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface    ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {

    // 1. 읽지 않은 메시지 조회
    // 메시지 조회
    // 1. 읽지 않은 메시지 조회
    // 메시지 조회
    List<ChatMessageEntity> findByChatRoomEntity_IdAndSenderId_IdNotAndIsReadFalse(Long chatRoomId, String currentUserId);
    int countByChatRoomEntityAndReceiverId_IdAndIsReadFalse(ChatRoomEntity chatRoom, String receiverId);

    // 읽음 처리
    @Modifying(clearAutomatically = true)
    @Query("UPDATE ChatMessageEntity m SET m.isRead = true WHERE m.chatRoomEntity.id = :chatRoomId AND m.senderId.id != :currentUserId AND m.isRead = false")
    void markAsReadByChatRoomIdAndUserId(@Param("chatRoomId") Long chatRoomId, @Param("currentUserId") String currentUserId);


    List<ChatMessageEntity> findByChatRoomEntity_IdOrderBySendAtAsc(Long roomId);

    Optional<ChatMessageEntity> findTopByChatRoomEntityOrderBySendAtDesc(ChatRoomEntity chatRoomEntity);

    List<ChatMessageEntity> findByChatRoomEntity(ChatRoomEntity chatRoomEntity);
}
