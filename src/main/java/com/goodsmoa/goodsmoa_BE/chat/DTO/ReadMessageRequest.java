package com.goodsmoa.goodsmoa_BE.chat.DTO;

import lombok.Data;

@Data
public class ReadMessageRequest {
    private Long chatRoomId;

    private Long lastReadMessageId;

    private String userId;
}
