package com.goodsmoa.goodsmoa_BE.community.controller;

import com.goodsmoa.goodsmoa_BE.community.dto.CommunityPostRequest;
import com.goodsmoa.goodsmoa_BE.community.dto.CommunityPostResponse;
import com.goodsmoa.goodsmoa_BE.community.service.CommunityLikeService;
import com.goodsmoa.goodsmoa_BE.community.service.CommunityService;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community/like")
public class CommunityLikeController {

    private final CommunityLikeService communityLikeService;

    // 해당 글에 대한 좋아요 토글
    @PostMapping("/post/{id}")
    public ResponseEntity<String> toggleLike(@AuthenticationPrincipal UserEntity user,
                                             @PathVariable Long id) {
        return communityLikeService.toggleLike(user, id);
    }
}
