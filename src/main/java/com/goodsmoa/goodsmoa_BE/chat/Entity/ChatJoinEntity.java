package com.goodsmoa.goodsmoa_BE.chat.Entity;

import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class ChatJoinEntity {

    // 채팅방 참여하는 아이디
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Boolean status; // 사용자가 채팅방을 나갔을 때 사용

    // 채팅방 정보 불러오는 엔티티
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id" , nullable = false)
    private ChatRoomEntity chatRoomEntity;

    // 채팅방 참여하는 유저
    @ManyToOne
    @JoinColumn(name = "user_id" , nullable = false)
    private UserEntity user;
}
