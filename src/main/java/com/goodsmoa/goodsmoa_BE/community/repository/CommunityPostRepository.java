package com.goodsmoa.goodsmoa_BE.community.repository;

import com.goodsmoa.goodsmoa_BE.community.entity.CommunityPostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityPostRepository extends JpaRepository<CommunityPostEntity, Long> {
}
