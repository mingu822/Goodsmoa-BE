package com.goodsmoa.goodsmoa_BE.chat.DTO;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom {
    private Long id;
    private String title;   // 채팅방 제목
    private Boolean status; // 채팅방 활성화 여부
    private String senderId;
    private String receiverId;


}
