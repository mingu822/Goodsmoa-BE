package com.goodsmoa.goodsmoa_BE.chat.Entity;

import com.goodsmoa.goodsmoa_BE.chat.DTO.ChatMessage;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Table(name="chat_message_entity")
public class ChatMessageEntity {

    // 메세지 보낸 아이디
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 메세지 내용
    @Column(nullable = false , columnDefinition = "TEXT")
    private String content;

    @Setter
    private Boolean isRead = false;

    // 메세지 타입 글,이미지,영상
    @Column(nullable = false )
    @Enumerated(EnumType.STRING)
    private ChatMessage.MessageType type;

    // 메세지 보낸시간
    @Column(nullable = false )
    private LocalDateTime sendAt;

    // 메세지 보낸 사람 아이디
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private UserEntity senderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private UserEntity receiverId; // 👈 추가! (받는 사람)


    // 채팅방 아이디
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id")
    private ChatRoomEntity chatRoomEntity;
}
