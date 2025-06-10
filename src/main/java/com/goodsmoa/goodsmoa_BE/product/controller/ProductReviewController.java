package com.goodsmoa.goodsmoa_BE.product.controller;

import com.goodsmoa.goodsmoa_BE.product.dto.post.PostDetailResponse;
import com.goodsmoa.goodsmoa_BE.product.dto.review.ProductReviewRequest;
import com.goodsmoa.goodsmoa_BE.product.dto.review.ProductSummaryResponse;
import com.goodsmoa.goodsmoa_BE.product.service.ProductReviewService;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/product-review")
@RequiredArgsConstructor
public class ProductReviewController {

    private final ProductReviewService productReviewService;

    /** ✅ 리뷰 작성 페이지 진입 (상품 정보 제공) */
    @GetMapping("/{productId}")
    public ResponseEntity<ProductSummaryResponse> getReviewWritePage(@PathVariable Long productId) {
        return productReviewService.getView(productId);
    }

    // 리뷰 쓰기
    @PostMapping("/create")
    public ResponseEntity<PostDetailResponse> createReview(@RequestPart ProductReviewRequest request,
                                                           @AuthenticationPrincipal UserEntity user,
                                                           @RequestPart("reviewImages") List<MultipartFile> reviewImages){
        return productReviewService.createReview(request, user, reviewImages);
    }

    // 리뷰 수정
    @PutMapping("/update")
    public ResponseEntity<PostDetailResponse> updateReview(@AuthenticationPrincipal UserEntity user, @RequestBody ProductReviewRequest request){
        return productReviewService.updateReview(request, user);
    }

    // 리뷰 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id, @AuthenticationPrincipal UserEntity user){
        return productReviewService.deleteReview(id, user);
    }

}
