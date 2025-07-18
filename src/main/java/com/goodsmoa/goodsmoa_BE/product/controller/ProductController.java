package com.goodsmoa.goodsmoa_BE.product.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.goodsmoa.goodsmoa_BE.enums.Board;
import com.goodsmoa.goodsmoa_BE.search.dto.SearchDocWithUserResponse;
import com.goodsmoa.goodsmoa_BE.search.service.SearchService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
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
    private final SearchService searchService;

    // 상품글 추가
    @PostMapping(value = "/post-create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostDetailResponse> createPost(
            @AuthenticationPrincipal UserEntity user,
            @RequestPart("postRequest") PostRequest request,
            @RequestPart("thumbnailImage") MultipartFile thumbnailImage,
            @RequestPart(value = "productImages", required = false) List<MultipartFile> productImages,
            @RequestPart(value = "contentImages", required = false) List<MultipartFile> contentImages) throws IOException {
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
            @RequestPart("deleteProductImageIds") String deleteProductImageIdsJson,
            @RequestPart("deleteDeliveryIds") String deleteDeliveryIds) throws IOException {
        return productService.updateProductPost(user, id, request, newThumbnailImage,
                newContentImages,
                newProductImages,
                deleteProductImageIdsJson,
                deleteDeliveryIds);
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
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return productService.getProductPostList(pageable);
    }

    // 사용자가 올린 상품글 리스트 조회
    @GetMapping("/post-user")
    public ResponseEntity<Page<PostsResponse>> getProductPostList(
            @AuthenticationPrincipal UserEntity user,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return productService.getProductPostListUser(user,pageable);
    }

    // 키워드 검색
    @GetMapping
    public ResponseEntity<Page<SearchDocWithUserResponse>> findByKeyword
    (
            @RequestParam(defaultValue = "ALL", name = "search_type") String searchType,
            @RequestParam Optional<String> query,
            @RequestParam Optional<Integer> category,
            @RequestParam(defaultValue = "new", name = "order_by") String orderBy,
            @RequestParam(required = false, defaultValue = "false", name = "include_expired")  boolean includeExpired,
            @RequestParam(required = false, defaultValue = "false", name = "include_scheduled")  boolean includeScheduled,
            @RequestParam(defaultValue = "0", name = "page") int page,
            @RequestParam(defaultValue = "0", name = "page_size") int pageSize
    )
    {
        return ResponseEntity.ok(
                searchService.detailedSearch(
                        searchType,
                        query.orElse(null),
                        Board.PRODUCT,
                        category.orElse(0),
                        orderBy,
                        includeExpired,
                        includeScheduled,
                        page,
                        pageSize
                )
        );
    }
}
