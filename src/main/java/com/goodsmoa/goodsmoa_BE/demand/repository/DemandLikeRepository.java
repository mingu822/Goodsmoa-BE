package com.goodsmoa.goodsmoa_BE.demand.repository;

import com.goodsmoa.goodsmoa_BE.demand.entity.DemandLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface DemandLikeRepository extends JpaRepository<DemandLikeEntity, Long> {

    Optional<DemandLikeEntity> findByUserIdAndPostId(String userId, Long postId);
    boolean existsByUserIdAndPostId(String userId, Long postId);
    Set<Long> findPostIdsByUserId(String userId);
    Long countByPostId(Long postId);

}
