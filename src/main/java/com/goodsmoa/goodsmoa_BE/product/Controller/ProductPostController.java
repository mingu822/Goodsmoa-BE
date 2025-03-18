package com.goodsmoa.goodsmoa_BE.product.Controller;

import com.goodsmoa.goodsmoa_BE.product.DTO.Post.*;
import com.goodsmoa.goodsmoa_BE.product.Service.ProductPostService;
import com.goodsmoa.goodsmoa_BE.user.Entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/productPost")
@RequiredArgsConstructor
public class ProductPostController {

    private final ProductPostService productPostService;

    // 임시로 글을 저장해야 상품을 추가시킬 수 있음
    @PostMapping("/save")
    public ResponseEntity<SavePostResponse> SaveProductPost(@AuthenticationPrincipal User user, @RequestBody SavePostRequest request){
        return productPostService.saveProductPost(user,request);
    }

    // 임시 저장된 글을 불러와서 상품을 추가하고 db에 저장 시켜야 됨
    @PostMapping("/create")
    public ResponseEntity<PostResponse> createProductPost(@AuthenticationPrincipal User user, @RequestBody PostRequest request){
        return productPostService.createProductPost(user,request);
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<PostDetailResponse> detailProductPost(@PathVariable Long id){
        return productPostService.detailProductPost(id);
    }

}