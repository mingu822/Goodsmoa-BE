package com.goodsmoa.goodsmoa_BE.chat.DTO;

import com.goodsmoa.goodsmoa_BE.user.DTO.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomResponse {
    private Long id;

    private String buyerId;

    private String sellerId;

    private UserInfo buyer;

    private UserInfo seller;

    private String sellerNickname;

    private String sellerProfileImage;

    private String lastMessageContent;
    private String lastMessageReceiverId;
    private int unreadCount;
    private LocalDateTime lastMessageTime;
}
