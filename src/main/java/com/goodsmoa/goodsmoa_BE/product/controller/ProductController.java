package com.goodsmoa.goodsmoa_BE.product.controller;

import com.goodsmoa.goodsmoa_BE.product.dto.Delivery.ProductDeliveryRequest;
import com.goodsmoa.goodsmoa_BE.product.dto.Delivery.ProductDeliveryResponse;
import com.goodsmoa.goodsmoa_BE.product.dto.Post.*;
import com.goodsmoa.goodsmoa_BE.product.dto.ProductRequest;
import com.goodsmoa.goodsmoa_BE.product.dto.ProductResponse;
import com.goodsmoa.goodsmoa_BE.product.service.ProductService;
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
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    // 상품글 추가
    @PostMapping("/post-create")
    public ResponseEntity<PostDetailResponse> createProduct(@AuthenticationPrincipal UserEntity user, @RequestBody PostRequest request){
        return productService.createPost(user, request);
    }

    // 생성한 상품글을 수정
    @PutMapping("/post-update")
    public ResponseEntity<PostResponse> updateProductPost(@AuthenticationPrincipal UserEntity user, @RequestBody PostRequest request){
        return productService.updateProductPost(user,request);
    }

    // 상품글 상세 조회
    @GetMapping("/post-detail/{id}")
    public ResponseEntity<PostDetailResponse> detailProductPost(@PathVariable Long id){
        return productService.detailProductPost(id);
    }

    // 상품글 삭제
    @DeleteMapping("/post-delete/{id}")
    public ResponseEntity<String> deleteProductPost(@AuthenticationPrincipal UserEntity user, @PathVariable Long id){
        return productService.deleteProductPost(user,id);
    }

    // 상품글 리스트 조회
    @GetMapping("/post")
    public ResponseEntity<Page<PostsResponse>> getProductPostList(
            @PageableDefault(size = 10, sort = "createAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return productService.getProductPostList(pageable);
    }

    // TODO 필요시 사용
//    // 상품 추가
//    @PostMapping("/create")
//    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest request){
//        return productService.createProduct(request);
//    }
//
//    // 상품 수정
//    @PutMapping("/update")
//    public ResponseEntity<ProductResponse> updateProduct(@AuthenticationPrincipal UserEntity user, @RequestBody ProductRequest request){
//        return productService.updateProduct(user,request);
//    }
//
//    // 상품 삭제
//    @DeleteMapping("/delete/{id}")
//    public ResponseEntity<String> deleteProduct(@AuthenticationPrincipal UserEntity user, Long id){
//        return productService.deleteProduct(user, id);
//    }
//
//    // 배송 방식 추가
//    @PostMapping("/delivery-create")
//    public ResponseEntity<ProductDeliveryResponse> createProductDelivery(@RequestBody ProductDeliveryRequest request) {
//        return productService.createProductDelivery(request);
//    }
//
//    // 배송 방식 수정
//    @PutMapping("/delivery-update")
//    public ResponseEntity<ProductDeliveryResponse> updateProductDelivery(@AuthenticationPrincipal UserEntity user, @RequestBody ProductDeliveryRequest request){
//        return productService.updateProductDelivery(user,request);
//    }
//
//    // 배송 방식 삭제
//    @DeleteMapping("/delivery-delete/{id}")
//    public ResponseEntity<String> deleteProductDelivery(@AuthenticationPrincipal UserEntity user, Long id){
//        return productService.deleteProductDelivery(user, id);
//    }

}
