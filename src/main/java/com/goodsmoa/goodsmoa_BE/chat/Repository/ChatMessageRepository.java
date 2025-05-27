package com.goodsmoa.goodsmoa_BE.chat.Repository;

import com.goodsmoa.goodsmoa_BE.chat.Entity.ChatMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface    ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {

}
