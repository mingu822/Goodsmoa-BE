package com.goodsmoa.goodsmoa_BE.community.repository;

import com.goodsmoa.goodsmoa_BE.community.entity.CommunityPostEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityPostRepository extends JpaRepository<CommunityPostEntity, Long> {
    Page<CommunityPostEntity> findByUser(UserEntity user, Pageable pageable);
}
