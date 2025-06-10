package com.goodsmoa.goodsmoa_BE.product.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.goodsmoa.goodsmoa_BE.product.dto.post.PostDetailResponse;
import com.goodsmoa.goodsmoa_BE.product.dto.post.PostRequest;
import com.goodsmoa.goodsmoa_BE.product.dto.post.PostsResponse;
import com.goodsmoa.goodsmoa_BE.product.service.ProductService;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    // 상품글 추가
    @PostMapping(value = "/post-create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostDetailResponse> createPost(
            @AuthenticationPrincipal UserEntity user,
            @RequestPart("postRequest") PostRequest request,
            @RequestPart("thumbnailImage") MultipartFile thumbnailImage,
            @RequestPart(value = "productImages", required = false) List<MultipartFile> productImages,
            @RequestPart(value = "contentImages", required = false) List<MultipartFile> contentImages) {
        return productService.createPost(user, request, thumbnailImage, productImages, contentImages);
    }

    // 생성한 상품글을 수정
    @PostMapping(value = "/post-update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostDetailResponse> updateProductPost(
            @AuthenticationPrincipal UserEntity user,
            @PathVariable Long id,
            @RequestPart("postRequest") PostRequest request,
            @RequestPart(value = "newThumbnailImage", required = false) MultipartFile newThumbnailImage,
            @RequestPart(value = "newContentImages", required = false) List<MultipartFile> newContentImages,
            @RequestPart(value = "newProductImages", required = false) List<MultipartFile> newProductImages,
            @RequestPart(value = "deleteContentImageIds", required = false) List<String> deleteContentImageIds,
            @RequestPart(value = "deleteProductImageIds", required = false) List<Long> deleteProductImageIds) {
        return productService.updateProductPost(user, id, request, newThumbnailImage,
                newContentImages,
                newProductImages,
                deleteContentImageIds,
                deleteProductImageIds);
    }

    // 상품글 상세 조회
    @GetMapping("/post-detail/{id}")
    public ResponseEntity<PostDetailResponse> detailProductPost(@PathVariable Long id) {
        return productService.detailProductPost(id);
    }

    // 상품글 삭제
    @DeleteMapping("/post-delete/{id}")
    public ResponseEntity<String> deleteProductPost(
            @AuthenticationPrincipal UserEntity user,
            @PathVariable Long id) {

        productService.deleteProductPost(user, id);

        return ResponseEntity.ok("게시물이 성공적으로 삭제되었습니다.");
    }

    // 상품글 리스트 조회
    @GetMapping("/post")
    public ResponseEntity<Page<PostsResponse>> getProductPostList(
            @PageableDefault(size = 10, sort = "createAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return productService.getProductPostList(pageable);
    }
}
