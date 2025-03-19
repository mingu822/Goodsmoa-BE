package com.goodsmoa.goodsmoa_BE.product.Controller;

import com.goodsmoa.goodsmoa_BE.product.DTO.Delivery.ProductDeliveryRequest;
import com.goodsmoa.goodsmoa_BE.product.DTO.Delivery.ProductDeliveryResponse;
import com.goodsmoa.goodsmoa_BE.product.DTO.Post.*;
import com.goodsmoa.goodsmoa_BE.product.DTO.ProductRequest;
import com.goodsmoa.goodsmoa_BE.product.DTO.ProductResponse;
import com.goodsmoa.goodsmoa_BE.product.Service.ProductService;
import com.goodsmoa.goodsmoa_BE.user.Entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    // 상품 추가
    @PostMapping("/create")
    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest request){
        return productService.createProduct(request);
    }

    // 임시로 글을 저장해야 상품을 추가시킬 수 있음
    @PostMapping("/post-save")
    public ResponseEntity<SavePostResponse> SaveProductPost(@AuthenticationPrincipal User user, @RequestBody SavePostRequest request){
        return productService.saveProductPost(user,request);
    }

    // 임시 저장된 글을 불러와서 상품을 추가하고 db에 저장 시켜야 됨
    @PostMapping("/post-create")
    public ResponseEntity<PostResponse> createProductPost(@AuthenticationPrincipal User user, @RequestBody PostRequest request){
        return productService.createProductPost(user,request);
    }

    // 상품글 상세 조회
    @GetMapping("/post-detail/{id}")
    public ResponseEntity<PostDetailResponse> detailProductPost(@PathVariable Long id){
        return productService.detailProductPost(id);
    }

    // 배송 방식 추가
    @PostMapping("/delivery-create")
    public ResponseEntity<ProductDeliveryResponse> createProductDelivery(@RequestBody ProductDeliveryRequest request){
        return productService.createProductDelivery(request);
    }
}