package com.goodsmoa.goodsmoa_BE.chat.Service;

import com.goodsmoa.goodsmoa_BE.chat.Converter.ChatRoomConverter;
import com.goodsmoa.goodsmoa_BE.chat.DTO.ChatRoom;

import com.goodsmoa.goodsmoa_BE.chat.DTO.ChatRoomResponse;
import com.goodsmoa.goodsmoa_BE.chat.Entity.ChatRoomEntity;
import com.goodsmoa.goodsmoa_BE.chat.Repository.ChatRoomRepository;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import com.goodsmoa.goodsmoa_BE.user.Repository.UserRepository;
import com.goodsmoa.goodsmoa_BE.user.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    private final UserRepository userRepository;
    @Transactional
    public ResponseEntity<ChatRoomResponse> createChatRoom(ChatRoom chat) {
        // 1. 유저 조회
        UserEntity sender = userRepository.findById(chat.getSenderId())
                .orElseThrow(() -> new IllegalArgumentException("보내는 유저가 존재하지 않습니다."));
        UserEntity receiver = userRepository.findById(chat.getReceiverId())
                .orElseThrow(() -> new IllegalArgumentException("받는 유저가 존재하지 않습니다."));

        // 2. 중복 체크 (양방향)
        Optional<ChatRoomEntity> existingRoom = chatRoomRepository
                .findBySenderAndReceiver(sender, receiver)
                .or(() -> chatRoomRepository.findBySenderAndReceiver(receiver, sender));
        if (existingRoom.isPresent()) {
            throw new IllegalStateException("이미 존재하는 채팅방입니다.");
        }

        // 3. 엔티티 생성 및 저장
        ChatRoomEntity chatRoom = ChatRoomConverter.toEntity(chat, sender, receiver);
        ChatRoomEntity savedRoom = chatRoomRepository.save(chatRoom);

        // 4. 응답 DTO 생성
        ChatRoomResponse response = ChatRoomConverter.toResponse(savedRoom);
        return ResponseEntity.ok(response);
    }

    @Transactional
    public ResponseEntity<String> joinRoom(Long roomId, String userId) {
        ChatRoomEntity chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "유저가 존재하지 않습니다."));
        chatRoom.addParticipant(user);
        return ResponseEntity.ok("채팅방 참가 완료");
    }
    public ResponseEntity<ChatRoomResponse> createRandomRoom() {
        // 랜덤 문자열 생성 (예: UUID)
        String randomTitle = "방-" + UUID.randomUUID().toString().substring(0, 8);

        ChatRoomEntity chatRoom = ChatRoomEntity.builder()
                .title(randomTitle)
                .status(true)
                .build();

        chatRoom = chatRoomRepository.save(chatRoom);
        return ResponseEntity.ok(ChatRoomConverter.toResponse(chatRoom));
    }




    public List<ChatRoomEntity> getAllChatRooms() {
        return chatRoomRepository.findAll();
    }

}
