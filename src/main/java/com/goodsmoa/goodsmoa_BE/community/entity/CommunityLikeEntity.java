package com.goodsmoa.goodsmoa_BE.community.entity;

import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "community_like")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityLikeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private CommunityPostEntity post;

    // 좋아요한 유저 ID
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
}
