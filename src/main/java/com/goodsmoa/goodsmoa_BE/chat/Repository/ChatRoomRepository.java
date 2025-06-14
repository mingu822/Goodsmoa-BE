package com.goodsmoa.goodsmoa_BE.chat.Repository;

import com.goodsmoa.goodsmoa_BE.chat.Entity.ChatMessageEntity;
import com.goodsmoa.goodsmoa_BE.chat.Entity.ChatRoomEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity , Long> {
    // sender와 receiver 조합으로 기존 채팅방이 있는지 찾기
    Optional<ChatRoomEntity> findByBuyerAndSeller(UserEntity buyer, UserEntity seller);
    List<ChatRoomEntity> findByBuyerOrSeller(UserEntity buyer, UserEntity seller);
    // 채팅방별로 가장 최근 메시지 조회


//
}
