package com.goodsmoa.goodsmoa_BE.community.repository;

import com.goodsmoa.goodsmoa_BE.community.entity.CommunityLikeEntity;
import com.goodsmoa.goodsmoa_BE.community.entity.CommunityPostEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommunityLikeRepository  extends JpaRepository<CommunityLikeEntity, Long> {


    // 좋아요 눌렀는지 확인용( 눌럿으면 true , 아직 안눌렀으면 false)
    Optional<CommunityLikeEntity> findByPostAndUser(CommunityPostEntity post, UserEntity user);

    // 게시글의 좋아요 개수
    Long countByPost(CommunityPostEntity post);
}
