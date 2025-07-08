package com.goodsmoa.goodsmoa_BE.product.controller;

import com.goodsmoa.goodsmoa_BE.product.dto.like.ProductLikeResponse;
import com.goodsmoa.goodsmoa_BE.product.service.ProductLikeService;
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
@RequestMapping("/product-like")
public class ProductLikeController {

    private final ProductLikeService productLikeService;

    // 좋아요 추가
    @PostMapping("/{id}")
    public ResponseEntity<ProductLikeResponse> likeProduct(
            @AuthenticationPrincipal UserEntity user,
            @PathVariable Long id
            ){
        return productLikeService.likeProduct(user, id);
    }

    // 좋아요 취소
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> unlikeProduct(
            @AuthenticationPrincipal UserEntity user,
            @PathVariable Long id
    ) {
        return productLikeService.unlikeProduct(user, id);
    }

    // 좋아요 리스트로 가져오기
    @GetMapping("/likes")
    public ResponseEntity<Page<ProductLikeResponse>> getLikedPosts(
            @AuthenticationPrincipal UserEntity user,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return productLikeService.getPagedLiked(user, pageable);
    }

    // 글 하나에 내가 좋아요 한건지 체크
    @GetMapping("/my-likes/{id}")
    public ResponseEntity<ProductLikeResponse> getSingleLikedTrade(
            @AuthenticationPrincipal UserEntity user,
            @PathVariable("id") Long id) {
        ProductLikeResponse productLikeResponse = productLikeService.getSingleLiked(user, id);
        return ResponseEntity.ok(productLikeResponse);
    }

}
