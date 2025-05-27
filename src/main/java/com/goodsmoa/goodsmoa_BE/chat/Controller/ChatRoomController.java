package com.goodsmoa.goodsmoa_BE.chat.Controller;

import com.goodsmoa.goodsmoa_BE.chat.DTO.ChatRoom;
import com.goodsmoa.goodsmoa_BE.chat.Entity.ChatRoomEntity;
import com.goodsmoa.goodsmoa_BE.chat.Service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chatroom")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @PostMapping("/create")
    public ChatRoomEntity createChatRoom(@RequestBody ChatRoom chatRoom) {
        return chatRoomService.createChatRoom(chatRoom);
    }

    @GetMapping("/list")
    public List<ChatRoomEntity> getChatRoomList() {
        return chatRoomService.getAllChatRooms();
    }
}
