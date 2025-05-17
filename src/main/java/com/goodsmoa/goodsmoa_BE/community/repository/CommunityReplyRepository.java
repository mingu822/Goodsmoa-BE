package com.goodsmoa.goodsmoa_BE.community.repository;

import com.goodsmoa.goodsmoa_BE.community.entity.CommunityReplyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommunityReplyRepository extends JpaRepository<CommunityReplyEntity, Long> {

    // 특정 게시글에 달린 모든 댓글 조회
    //reply.post.id 기준 조회
    List<CommunityReplyEntity> findByPostIdOrderByCreatedAtAsc(Long postId);

    // 특정 게시글에 달린 댓글 개수 조회
    //reply.post.id 기준 조회
    Long countByPostId(Long postId);

}