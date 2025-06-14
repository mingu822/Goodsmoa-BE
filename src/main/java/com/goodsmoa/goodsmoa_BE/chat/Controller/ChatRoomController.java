package com.goodsmoa.goodsmoa_BE.chat.Controller;

import com.goodsmoa.goodsmoa_BE.chat.Converter.ChatRoomConverter;
import com.goodsmoa.goodsmoa_BE.chat.DTO.ChatRoom;
import com.goodsmoa.goodsmoa_BE.chat.DTO.ChatRoomResponse;
import com.goodsmoa.goodsmoa_BE.chat.Entity.ChatRoomEntity;
import com.goodsmoa.goodsmoa_BE.chat.Repository.ChatRoomRepository;
import com.goodsmoa.goodsmoa_BE.chat.Service.ChatRoomService;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/chatroom")
@RequiredArgsConstructor
@Slf4j
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @PostMapping("/create")
    public ResponseEntity<ChatRoomResponse> createChatRoom(@RequestBody ChatRoom chatRoom) {
        return chatRoomService.createChatRoom(chatRoom);
    }
    @PostMapping("/room/create/random")
    public ResponseEntity<ChatRoomResponse> createRandomRoom(@RequestBody Map<String, String> request) {
            String senderId = request.get("senderId");
            return chatRoomService.createRandomRoom(senderId);
        }

    // 테스트용 랜덤 채팅방 생성 (현재 로그인된 사용자 기준)
    @PostMapping("/room/create/random1")
    public ResponseEntity<ChatRoomResponse> createRandomRoom(@AuthenticationPrincipal UserEntity user) {
        return chatRoomService.createRandomRoom(user.getId());
    }

    @PostMapping("/room/{roomId}/join")
    public ResponseEntity<String> joinRoom(@PathVariable Long roomId, @AuthenticationPrincipal UserEntity user) {
            String userId = user.getId();
            return chatRoomService.joinRoom(roomId, userId);
        }
    @GetMapping("/rooms/list")
    public ResponseEntity<List<ChatRoomResponse>> getMyChatRooms(@AuthenticationPrincipal UserEntity user) {
        if (user == null) {
            log.warn("⚠️ 채팅방 목록 조회 요청: 인증된 사용자 정보가 없습니다.");
            return ResponseEntity.status(401).build(); // Unauthorized
        }
        log.info("ℹ️ 유저 [{}]의 채팅방 목록 조회 요청", user.getId());
        List<ChatRoomResponse> chatRooms = chatRoomService.getMyChatRooms(user);
        log.info("✅ 유저 [{}]에게 반환될 채팅방 목록 ({}개)", user.getId(), chatRooms.size());
        for (ChatRoomResponse room : chatRooms) {
            log.info("  ➡️ 채팅방 ID: {}, 상대: {}, 안 읽은 메시지 수: {}",
                    room.getId(),
                    room.getSellerNickname(), // 상대방 닉네임으로 확인
                    room.getUnreadCount());
        }
        return ResponseEntity.ok(chatRooms); // 200 OK와 함께 채팅방 목록 반환

    }
}
