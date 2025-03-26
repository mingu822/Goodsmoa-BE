package com.goodsmoa.goodsmoa_BE.chat.Entity;

import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class ChatRoomEntity {
    // 채팅방 아이디
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 채팅방 제목
    @Column(nullable = false , length = 15 )
    private String title;

    // 채팅방이 활성화 된건지 아닌지
    @Column(nullable = false)
    private Boolean status; // 진행(true) 종료(false)

    // 채팅방 만드는 사람의 유저아이디
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id" )
    private UserEntity user;
}

