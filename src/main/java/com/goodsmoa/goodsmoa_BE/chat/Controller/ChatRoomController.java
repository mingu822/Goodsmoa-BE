package com.goodsmoa.goodsmoa_BE.chat.Controller;

import com.goodsmoa.goodsmoa_BE.chat.Converter.ChatRoomConverter;
import com.goodsmoa.goodsmoa_BE.chat.DTO.ChatRoom;
import com.goodsmoa.goodsmoa_BE.chat.DTO.ChatRoomResponse;
import com.goodsmoa.goodsmoa_BE.chat.Entity.ChatRoomEntity;
import com.goodsmoa.goodsmoa_BE.chat.Repository.ChatRoomRepository;
import com.goodsmoa.goodsmoa_BE.chat.Service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/chatroom")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    private final ChatRoomRepository chatRoomRepository;

//    @PostMapping("/create")
//    public ResponseEntity<ChatRoomResponse> createChatRoom(@RequestBody ChatRoom chatRoom) {
//        return chatRoomService.createChatRoom(chatRoom);
//    }

    @GetMapping("/list")
    public List<ChatRoomEntity> getChatRoomList() {
        return chatRoomService.getAllChatRooms();
    }

    @GetMapping("/room/list")
    public ResponseEntity<List<ChatRoomResponse>> getRoomList() {
        List<ChatRoomEntity> rooms = chatRoomRepository.findAll();
        List<ChatRoomResponse> response = rooms.stream()
                .map(ChatRoomConverter::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }


    @PostMapping("/room/create/random")
    public ResponseEntity<ChatRoomResponse> createRandomRoom() {
        return chatRoomService.createRandomRoom();
    }

    @PostMapping("/room/{roomId}/join")
    public ResponseEntity<String> joinRoom(@PathVariable Long roomId, @RequestBody Map<String, String> request) {
        String userId = request.get("userId");
        return chatRoomService.joinRoom(roomId, userId);
    }

}
