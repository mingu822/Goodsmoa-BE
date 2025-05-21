package com.goodsmoa.goodsmoa_BE.community.entity;

import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "community_replys")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityReplyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 댓글 작성자 ID
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    // 어떤 게시글의 댓글인지
    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private CommunityPostEntity post;


    //부모 댓글
    // 자기 자신을 참조하는 부분 (!!nullable 허용)
    @ManyToOne
    @JoinColumn(name = "parents_id")
    private CommunityReplyEntity parentReply;

    // 댓글 내용
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;



    // 댓글 수정 메서드
    public void updateContent(String content) {
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }
}
