package com.goodsmoa.goodsmoa_BE.chat.Controller;

import com.goodsmoa.goodsmoa_BE.chat.DTO.ChatRoom;
import com.goodsmoa.goodsmoa_BE.chat.DTO.ChatRoomResponse;
import com.goodsmoa.goodsmoa_BE.chat.Entity.ChatRoomEntity;
import com.goodsmoa.goodsmoa_BE.chat.Repository.ChatRoomRepository;
import com.goodsmoa.goodsmoa_BE.chat.Service.ChatRoomService;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chatroom")
@RequiredArgsConstructor
public class ChatRoomController {
//
    private final ChatRoomService chatRoomService;

    private final ChatRoomRepository chatRoomRepository;

    @PostMapping("/create")
    public ResponseEntity<ChatRoomResponse> createChatRoom(@RequestBody ChatRoom chatRoom) {
        return chatRoomService.createChatRoom(chatRoom);
    }
    @PostMapping("/room/create/random")
    public ResponseEntity<ChatRoomResponse> createRandomRoom(@RequestBody Map<String, String> request) {
            String senderId = request.get("senderId");
            return chatRoomService.createRandomRoom(senderId);
        }

    @GetMapping("/list")
    public List<ChatRoomEntity> getChatRoomList() {
        return chatRoomService.getAllChatRooms();
    }

    @PostMapping("/room/{roomId}/join")
    public ResponseEntity<String> joinRoom(@PathVariable Long roomId, @AuthenticationPrincipal UserEntity user) {
            String userId = user.getId();
            return chatRoomService.joinRoom(roomId, userId);
        }
}
