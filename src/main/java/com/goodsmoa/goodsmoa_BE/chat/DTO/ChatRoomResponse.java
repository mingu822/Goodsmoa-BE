package com.goodsmoa.goodsmoa_BE.chat.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomResponse {
    private Long id;

    private String title;

}
