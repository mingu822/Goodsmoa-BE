package com.goodsmoa.goodsmoa_BE.demand.controller;

import com.goodsmoa.goodsmoa_BE.demand.dto.post.*;
import com.goodsmoa.goodsmoa_BE.demand.service.DemandLikeService;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/demand/like")
@RequiredArgsConstructor
public class DemandLikeApiController {

    private final DemandLikeService demandLikeService;

    // 로그인한 유저가 작성한 글 목록
    @GetMapping("/user")
    public ResponseEntity<Page<DemandPostResponse>> findByUser(
            @AuthenticationPrincipal UserEntity user,
            @RequestParam Optional<Integer> category,
            @RequestParam(defaultValue = "0", name = "page") int page,
            @RequestParam(defaultValue = "10", name = "page_size") int pageSize
    ) {
        DemandSearchRequest searchRequest = new DemandSearchRequest(category.orElse(0), page, pageSize);
        return ResponseEntity.ok(demandLikeService.getDemandPostListByUser(user, searchRequest));
    }

    // 좋아요
    @PostMapping(value = "/{id}")
    public ResponseEntity<?> toggleLike(
            @AuthenticationPrincipal UserEntity user,
            @PathVariable("id") Long postId)
    {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "로그인이 필요합니다")); //로그인이 필요합니다
        }
        try {
            //TODO : 로그인 페이지로 이동
            String result = demandLikeService.toggleLike(user, postId);
            return ResponseEntity.ok(Map.of("message", result)); //좋아요 완료
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage())); //존재하지 않는 게시물입니다
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "서버 오류: " + e.getMessage())); //서버 오류...
        }
    }
    
    // 좋아요 
    @PostMapping(value = "/{id}/create")
    public ResponseEntity<String> create(
            @AuthenticationPrincipal UserEntity user,
            @PathVariable("id") Long postId)
    {
        if(user==null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다");
        demandLikeService.likePost(user, postId);
        return ResponseEntity.ok().build();
    }

    // 좋아요 취소
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<String> delete(
            @AuthenticationPrincipal UserEntity user,
            @PathVariable("id") Long postId)
    {
        if(user==null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다");
        demandLikeService.unlikePost(user, postId);
        return ResponseEntity.ok().build();
    }
}
