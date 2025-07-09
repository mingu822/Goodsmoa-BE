package com.goodsmoa.goodsmoa_BE.commission.controller;

import com.goodsmoa.goodsmoa_BE.commission.dto.like.CommissionLikeResponse;
import com.goodsmoa.goodsmoa_BE.commission.service.CommissionLikeService;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/commission-like")
public class CommissionLikeController {

    private final CommissionLikeService commissionLikeService;

    // 찜 추가
    @PostMapping("/{id}")
    public ResponseEntity<Void> commissionLike(@AuthenticationPrincipal UserEntity user,
                                               @PathVariable Long id){
        return commissionLikeService.like(user, id);
    }

    // 찜 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommissionLike(@AuthenticationPrincipal UserEntity user,
                                                     @PathVariable Long id){
        return commissionLikeService.unLike(user,id);
    }

    // 내가 찜 한 글 페이지로 가져오기
    @GetMapping("/likes")
    public ResponseEntity<Page<CommissionLikeResponse>> likeCommission(
            @AuthenticationPrincipal UserEntity user,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ){
        return commissionLikeService.getLikes(user,pageable);
    }

    // 한 개의 글 찜 여부 체크
    @GetMapping("/my-like/{id}")
    public ResponseEntity<CommissionLikeResponse> getSingleCommissionLike(
            @AuthenticationPrincipal UserEntity user,
            @PathVariable("id") Long id
    ){
        return commissionLikeService.getSingleLiked(user,id);
    }

}
