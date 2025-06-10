package com.goodsmoa.goodsmoa_BE.product.controller;

import com.goodsmoa.goodsmoa_BE.product.dto.like.ProductLikeResponse;
import com.goodsmoa.goodsmoa_BE.product.service.ProductLikeService;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product-like")
public class ProductLikeController {

    private final ProductLikeService productLikeService;

    @PostMapping("/{id}")
    public ResponseEntity<ProductLikeResponse> likeProduct(
            @AuthenticationPrincipal UserEntity user,
            @PathVariable Long id
            ){
        return productLikeService.likeProduct(user, id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> unlikeProduct(
            @AuthenticationPrincipal UserEntity user,
            @PathVariable Long id
    ) {
        return productLikeService.unlikeProduct(user, id);
    }

}
