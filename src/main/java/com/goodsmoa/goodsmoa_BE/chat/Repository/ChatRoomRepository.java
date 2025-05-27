package com.goodsmoa.goodsmoa_BE.chat.Repository;

import com.goodsmoa.goodsmoa_BE.chat.Entity.ChatRoomEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity , Long> {
    // sender와 receiver 조합으로 기존 채팅방이 있는지 찾기
    Optional<ChatRoomEntity> findBySenderAndReceiver(UserEntity sender, UserEntity receiver);
//
}
