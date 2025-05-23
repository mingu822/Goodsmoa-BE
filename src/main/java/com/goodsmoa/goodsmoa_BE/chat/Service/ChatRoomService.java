package com.goodsmoa.goodsmoa_BE.chat.Service;

import com.goodsmoa.goodsmoa_BE.chat.DTO.ChatRoom;

import com.goodsmoa.goodsmoa_BE.chat.Entity.ChatRoomEntity;
import com.goodsmoa.goodsmoa_BE.chat.Repository.ChatRoomRepository;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import com.goodsmoa.goodsmoa_BE.user.Repository.UserRepository;
import com.goodsmoa.goodsmoa_BE.user.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    private final UserRepository userRepository;
    @Transactional
    public ChatRoomEntity createChatRoom(ChatRoom chat) {
        // sender, receiver ID로 유저 조회
        UserEntity sender = userRepository.findById(chat.getSenderId())
                .orElseThrow(() -> new IllegalArgumentException("보내는 유저가 존재하지 않습니다."));
        UserEntity receiver = userRepository.findById(chat.getReceiverId())
                .orElseThrow(() -> new IllegalArgumentException("받는 유저가 존재하지 않습니다."));

        // 중복 채팅방 체크
        Optional<ChatRoomEntity> existingRoom = chatRoomRepository
                .findBySenderAndReceiver(sender, receiver)
                .or(() -> chatRoomRepository.findBySenderAndReceiver(receiver, sender));
        if (existingRoom.isPresent()) {
            throw new IllegalStateException("이미 존재하는 채팅방입니다.");
        }

        // 채팅방 생성
        ChatRoomEntity chatRoom = ChatRoomEntity.builder()
                .title(chat.getTitle())
                .status(true)
                .sender(sender)
                .receiver(receiver)
                .build();

        return chatRoomRepository.save(chatRoom);
    }

    public List<ChatRoomEntity> getAllChatRooms() {
        return chatRoomRepository.findAll();
    }

}
