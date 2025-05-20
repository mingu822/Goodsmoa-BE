package com.goodsmoa.goodsmoa_BE.community.controller;

import com.goodsmoa.goodsmoa_BE.community.dto.CommunityPostResponse;
import com.goodsmoa.goodsmoa_BE.community.dto.CommunityReplyRequest;
import com.goodsmoa.goodsmoa_BE.community.dto.CommunityReplyResponse;
import com.goodsmoa.goodsmoa_BE.community.dto.MyReplySimpleResponse;
import com.goodsmoa.goodsmoa_BE.community.service.CommunityReplyService;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community/reply")
public class CommunityReplyController {

    private final CommunityReplyService service;

    // 댓글 등록
    @PostMapping
    public ResponseEntity<CommunityPostResponse> createReply(@AuthenticationPrincipal UserEntity user,
                                                             @RequestBody CommunityReplyRequest request) {
        return ResponseEntity.ok(service.createReply(user, request));
    }

    // 댓글 수정
    @PutMapping("/{id}")
    public ResponseEntity<CommunityPostResponse> updateReply(@AuthenticationPrincipal UserEntity user,
                                              @PathVariable Long id,
                                              @RequestBody String content) {
        service.updateReply(user, id, content);
        return ResponseEntity.ok(service.updateReply(user, id, content));
    }

    // 댓글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteReply(@AuthenticationPrincipal UserEntity user,
                                              @PathVariable Long id) {
        service.deleteReply(user, id);
        return ResponseEntity.ok("댓글 삭제 완료");
    }


    // 댓글 목록 조회(글 기준) (아직 필요X)
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommunityReplyResponse>> getReplies(@PathVariable Long postId) {
        return ResponseEntity.ok(service.getReplies(postId));
    }

    @GetMapping("/my")
    public ResponseEntity<Page<MyReplySimpleResponse>> getMyReplies(@AuthenticationPrincipal UserEntity user,
                                                                    @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(service.getMyReplies(user, page));
    }

}
