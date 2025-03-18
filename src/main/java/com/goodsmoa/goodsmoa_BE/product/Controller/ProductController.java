package com.goodsmoa.goodsmoa_BE.product.Controller;

import com.goodsmoa.goodsmoa_BE.product.DTO.ProductRequest;
import com.goodsmoa.goodsmoa_BE.product.DTO.ProductResponse;
import com.goodsmoa.goodsmoa_BE.product.Service.ProductService;
import com.goodsmoa.goodsmoa_BE.user.Entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    @PostMapping("/create")
    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest request){
        return productService.createProduct(request);
    }

}
