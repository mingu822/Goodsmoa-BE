package com.goodsmoa.goodsmoa_BE.community.controller;

import com.goodsmoa.goodsmoa_BE.community.dto.CommunityPostRequest;
import com.goodsmoa.goodsmoa_BE.community.dto.CommunityPostResponse;
import com.goodsmoa.goodsmoa_BE.community.dto.CommunityPostSimpleResponse;
import com.goodsmoa.goodsmoa_BE.community.service.CommunityService;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community/post")
public class CommunityController {

    private final CommunityService service;

    //  글 작성
    @PostMapping
    public ResponseEntity<CommunityPostResponse> createPost(@AuthenticationPrincipal UserEntity user,
                                                            @RequestBody CommunityPostRequest request) {
        return service.createPost(user, request);
    }

    //  글 수정ㅅ
    @PutMapping("/{id}")
    public ResponseEntity<CommunityPostResponse> updatePost(@AuthenticationPrincipal UserEntity user,
                                                            @PathVariable Long id,
                                                            @RequestBody CommunityPostRequest request) {
        return service.updatePost(user, id, request);
    }

    // 글 조회 (조회수 Redis 증가 포함)
    @GetMapping("/{id}")
    public ResponseEntity<CommunityPostResponse> getPost(@PathVariable Long id) {
        return service.getPost(id);
    }

    // 글 삭제 (본인 + 관리자 가능)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(@AuthenticationPrincipal UserEntity user,
                                             @PathVariable Long id) {
        return service.deletePost(user, id);
    }

    // 전체 글 페이징 조회 (기본: 0페이지 20개)
    @GetMapping
    public ResponseEntity<Page<CommunityPostSimpleResponse>> getAllPosts(@RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(service.getAllPosts(page));
    }

    // 내가 쓴 글 페이징 조회
    @GetMapping("/my")
    public ResponseEntity<Page<CommunityPostSimpleResponse>> getMyPosts(@AuthenticationPrincipal UserEntity user,
                                                                        @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(service.getMyPosts(user, page));
    }



}
