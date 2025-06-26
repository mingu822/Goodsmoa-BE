package com.goodsmoa.goodsmoa_BE.product.controller;

import com.goodsmoa.goodsmoa_BE.product.dto.post.PostDetailResponse;
import com.goodsmoa.goodsmoa_BE.product.dto.review.*;
import com.goodsmoa.goodsmoa_BE.product.service.ProductReviewService;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/product-review")
@RequiredArgsConstructor
public class ProductReviewController {

    private final ProductReviewService productReviewService;

    // 단일 리뷰 조회
    @GetMapping("/{reviewId}")
    public ResponseEntity<ProductReviewDetailResponse> getReviewDetail(@PathVariable Long reviewId) {
        return productReviewService.getView(reviewId);
    }

    // 상품글에 있는 리뷰들 조회
    @GetMapping("/post/{postId}")
    public ResponseEntity<Page<ProductSummaryResponse>> getReviewsByPost(
            @PathVariable Long postId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return productReviewService.getReviewList(postId, pageable);
    }

    // 리뷰 쓰기
    @PostMapping("/create")
    public ResponseEntity<PostDetailResponse> createReview(@RequestPart ProductReviewRequest request,
                                                           @AuthenticationPrincipal UserEntity user,
                                                           @RequestPart("reviewImages") List<MultipartFile> reviewImages) throws IOException {
        return productReviewService.createReview(request, user, reviewImages);
    }

    // 리뷰 수정
    @PutMapping("/update")
    public ResponseEntity<PostDetailResponse> updateReview(@RequestPart("review") ProductReviewUpdateRequest request,
                                                           @AuthenticationPrincipal UserEntity user,
                                                           @RequestPart(value = "newImages", required = false) List<MultipartFile> newImages) throws IOException {
        return productReviewService.updateReview(request, user, newImages);
    }

    // 리뷰 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id, @AuthenticationPrincipal UserEntity user){
        return productReviewService.deleteReview(id, user);
    }

    // 내가 쓴 리뷰 리스트 조회
    @GetMapping("/list")
    public ResponseEntity<Page<ProductReviewResponse>> listOrder(
            @AuthenticationPrincipal UserEntity user,
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return productReviewService.getList(user, pageable);
    }


}
