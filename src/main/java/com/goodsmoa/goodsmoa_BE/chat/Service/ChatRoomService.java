package com.goodsmoa.goodsmoa_BE.chat.Service;

import com.goodsmoa.goodsmoa_BE.chat.Converter.ChatRoomConverter;
import com.goodsmoa.goodsmoa_BE.chat.DTO.ChatRoom;

import com.goodsmoa.goodsmoa_BE.chat.DTO.ChatRoomResponse;
import com.goodsmoa.goodsmoa_BE.chat.Entity.ChatRoomEntity;
import com.goodsmoa.goodsmoa_BE.chat.Repository.ChatRoomRepository;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import com.goodsmoa.goodsmoa_BE.user.Repository.UserRepository;
import com.goodsmoa.goodsmoa_BE.user.DTO.UserInfo;
import com.goodsmoa.goodsmoa_BE.user.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomConverter chatRoomConverter; // ✅ 컨버터 주입

    private final UserRepository userRepository;
    @Transactional
    public ResponseEntity<ChatRoomResponse> createChatRoom(ChatRoom chat) {
        // sender, receiver ID로 유저 조회
        UserEntity buyer = userRepository.findById(chat.getBuyerId())
                .orElseThrow(() -> new IllegalArgumentException("보내는 유저가 존재하지 않습니다."));
        UserEntity seller = userRepository.findById(chat.getSellerId())
                .orElseThrow(() -> new IllegalArgumentException("받는 유저가 존재하지 않습니다."));

        // 중복 채팅방 체크
        Optional<ChatRoomEntity> existingRoom = chatRoomRepository.findByBuyerAndSeller(buyer, seller)
                .or(() -> chatRoomRepository.findByBuyerAndSeller(seller, buyer)); // ✅ 이 부분이 중요합니다.
        if (existingRoom.isPresent()) {
            ChatRoomResponse response = ChatRoomConverter.toResponse(existingRoom.get());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);

        }

        // 채팅방 생성
        ChatRoomEntity chatRoom = ChatRoomConverter.toEntity(chat, buyer, seller);
        ChatRoomEntity savedRoom = chatRoomRepository.save(chatRoom);

        // 4. 응답 DTO 생성
        ChatRoomResponse response = ChatRoomConverter.toResponse(savedRoom);
        return ResponseEntity.ok(response);
    }
    @Transactional
    public ResponseEntity<ChatRoomResponse> createRandomRoom(String senderId) {
        // sender 조회
        UserEntity sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));

        // 다른 유저 중 무작위로 receiver 선택
        List<UserEntity> allUsers = userRepository.findAll();
        UserEntity receiver = allUsers.stream()
                .filter(u -> !u.getId().equals(senderId))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("다른 유저가 없습니다."));

        // buyer/seller 역할 무작위 할당
        UserEntity buyer = Math.random() < 0.5 ? sender : receiver;
        UserEntity seller = buyer == sender ? receiver : sender;

        ChatRoomEntity chatRoom = ChatRoomEntity.builder()
                .buyer(buyer)
                .seller(seller)
                .build();

        ChatRoomEntity savedRoom = chatRoomRepository.save(chatRoom);

        ChatRoomResponse response = ChatRoomConverter.toResponse(savedRoom);
        return ResponseEntity.ok(response);
    }

//    public List<ChatRoomEntity> getAllChatRooms() {
//                return chatRoomRepository.findAll();
//            }

    @Transactional
    public ResponseEntity<String> joinRoom(Long roomId, String userId) {
        ChatRoomEntity chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "유저가 존재하지 않습니다."));
        chatRoom.addParticipant(user);
        return ResponseEntity.ok("채팅방 참가 완료");
    }

    public List<ChatRoomResponse> getMyChatRooms(UserEntity currentUser) {
        List<ChatRoomEntity> chatRoomEntities = chatRoomRepository.findByBuyerOrSeller(currentUser, currentUser);
        return chatRoomEntities.stream()
                .map(room -> chatRoomConverter.toChatRoomResponse(room, currentUser))
                .collect(Collectors.toList());
    }


}
