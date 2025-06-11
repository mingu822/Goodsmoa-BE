package com.goodsmoa.goodsmoa_BE.chat.DTO;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom {
    private Long id;
    private String sellerId;
    private String buyerId;


}
