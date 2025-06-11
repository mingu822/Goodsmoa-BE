package com.goodsmoa.goodsmoa_BE.chat.Entity;

import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
@Table(name = "chatRoom")
public class ChatRoomEntity {
    // 채팅방 아이디
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 채팅방 만드는 사람의 유저아이디
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "seller_id", nullable = false)
    private UserEntity seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "buyer_id", nullable = false)
    private UserEntity buyer;

    private Long postId;
// 채팅 메시지 목록 (양방향 매핑 필요 시)
    // @OneToMany(mappedBy = "chatRoomEntity", cascade = CascadeType.ALL)
    // private List<ChatMessageEntity> messages;

    public void addParticipant(UserEntity user) {
        if(seller == null) {
            seller = user;
        }else if(buyer == null) {
            buyer = user;
        }
    }




}

