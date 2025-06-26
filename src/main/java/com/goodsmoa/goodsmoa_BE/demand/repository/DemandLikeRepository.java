package com.goodsmoa.goodsmoa_BE.demand.repository;

import com.goodsmoa.goodsmoa_BE.demand.entity.DemandLikeEntity;
import com.goodsmoa.goodsmoa_BE.demand.entity.DemandPostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface DemandLikeRepository extends JpaRepository<DemandLikeEntity, Long> {

    Optional<DemandLikeEntity> findByUserIdAndPostId(String userId, Long postId);
    boolean existsByUserIdAndPostId(String userId, Long postId);
    Page<DemandLikeEntity> findByUserId(String userId, Pageable pageable);
    Set<Long> findPostIdsByUserId(String userId);
    Long countByPostId(Long postId);

    @Query("SELECT l.postId FROM DemandLikeEntity l " +
            "WHERE l.userId = :userId " +
            "AND l.postId IN :postIds")
    Set<Long> findLikedIdsByUserAndPosts(
            @Param("userId") String userId,
            @Param("postIds") List<Long> postIds
    );
}
