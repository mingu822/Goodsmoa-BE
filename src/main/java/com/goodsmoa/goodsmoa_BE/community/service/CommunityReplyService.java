package com.goodsmoa.goodsmoa_BE.community.service;

import com.goodsmoa.goodsmoa_BE.community.converter.CommunityPostConverter;
import com.goodsmoa.goodsmoa_BE.community.converter.CommunityReplyConverter;
import com.goodsmoa.goodsmoa_BE.community.dto.CommunityPostResponse;
import com.goodsmoa.goodsmoa_BE.community.dto.CommunityReplyRequest;
import com.goodsmoa.goodsmoa_BE.community.dto.CommunityReplyResponse;
import com.goodsmoa.goodsmoa_BE.community.entity.CommunityPostEntity;
import com.goodsmoa.goodsmoa_BE.community.entity.CommunityReplyEntity;
import com.goodsmoa.goodsmoa_BE.community.repository.CommunityPostRepository;
import com.goodsmoa.goodsmoa_BE.community.repository.CommunityReplyRepository;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CommunityReplyService {

    private final CommunityReplyRepository replyRepository;
    private final CommunityPostRepository postRepository;
    private final CommunityReplyConverter converter;
    private final CommunityPostConverter postConverter;


    // 댓글 작업 후 글 상세 DTO를 만들어주는 공통 메서드
    public CommunityPostResponse buildPostResponse(Long postId) {
        CommunityPostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글 없음"));

        Long replyCount = replyRepository.countByPostId(postId);
        List<CommunityReplyResponse> replies = getReplies(postId);

        return postConverter.toResponseDto(post, replyCount, replies);
    }



    // 댓글 생성 → 글 상세 DTO 반환
    @Transactional
    public CommunityPostResponse createReply(UserEntity user, CommunityReplyRequest request) {
        CommunityPostEntity post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new RuntimeException("게시글 없음"));

        //부모댓글 id 있으면 부모댓글 설정해주고 없으면 부모댓글 null
        CommunityReplyEntity parent = null;
        if (request.getParentId() != null) {
            parent = replyRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("부모 댓글 없음"));
        }

        CommunityReplyEntity reply = CommunityReplyEntity.builder()
                .user(user)
                .post(post)
                .parentReply(parent)
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .build();

        replyRepository.save(reply);

        return buildPostResponse(post.getId());
    }

    // 댓글 수정(!!본인만 가능)
    @Transactional
    public CommunityPostResponse updateReply(UserEntity user, Long replyId, String content) {
        CommunityReplyEntity reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new RuntimeException("댓글 없음"));

        if (!reply.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("본인 댓글만 수정 가능");
        }

        reply.updateContent(content);

        return buildPostResponse(reply.getPost().getId());
    }

    // 댓글 삭제(!본인만 삭제가능)
    @Transactional
    public CommunityPostResponse deleteReply(UserEntity user, Long replyId) {
        CommunityReplyEntity reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new RuntimeException("댓글 없음"));

        if (!reply.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("본인 댓글만 삭제 가능");
        }

        Long postId = reply.getPost().getId();
        replyRepository.delete(reply);

        return buildPostResponse(postId);
    }

    // 댓글 목록 트리로 반환( 글 응답 DTO 에 댓글 리스트로 사용됨)
    //1.특정 게시글의 모든 댓글을 가져온다.
    //2.각 댓글을 DTO로 바꾸고 Map<댓글ID, DTO>에 저장.
    //3.parentId를 기준으로 댓글들을 계층 구조로 구성한다.
    //4.최상위 댓글(부모가 없는 댓글)만 roots 리스트에 담아 리턴한다.
    public List<CommunityReplyResponse> getReplies(Long postId) {
        // 1. DB에서 해당 게시글의 모든 댓글을 시간순으로 가져오기
        List<CommunityReplyEntity> replies = replyRepository.findByPostIdOrderByCreatedAtAsc(postId);

        // 2. 댓글 ID로 빠르게 찾기 위한 맵 (id → DTO)
        //( Map:Key-Value 쌍 저장하는 자료구조)
        Map<Long, CommunityReplyResponse> map = new HashMap<>();

        // 3. 최상위 댓글들을 담을 리스트 (즉, parentId == null 인 댓글들)
        List<CommunityReplyResponse> roots = new ArrayList<>();

        // 4. 엔티티들을 DTO로 변환하고 map에 담아둠 (나중에 부모를 찾아서 children에 넣기 위함)
        for (CommunityReplyEntity reply : replies) {
            CommunityReplyResponse dto = converter.toDto(reply);
            // 댓글 ID를 key로,dto를 value 로 저장
            map.put(reply.getId(), dto);
        }

        // 5. 트리 구조 만들기: 부모 댓글의 children 리스트에 현재 댓글을 넣는다
        for (CommunityReplyEntity reply : replies) {
            CommunityReplyResponse dto = map.get(reply.getId());

            // 5-1. 부모가 있는 댓글이면 → 부모의 children 리스트에 추가
            if (reply.getParentReply() != null) {
                Long parentId = reply.getParentReply().getId();
                map.get(parentId).getChildren().add(dto);

                // 5-2. 부모가 없는 댓글이면 → 최상위 댓글이므로 roots에 추가
            } else {
                roots.add(dto);
            }
        }

        // 6. 최종적으로 트리 구조를 이룬 최상위 댓글 리스트를 반환
        return roots;
    }


    // 댓글 수 조회
    public Long countReplies(Long postId) {
        return replyRepository.countByPostId(postId);
    }
}
