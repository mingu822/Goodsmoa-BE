package com.goodsmoa.goodsmoa_BE.product.Service;

import com.goodsmoa.goodsmoa_BE.product.Converter.ProductConverter;
import com.goodsmoa.goodsmoa_BE.product.DTO.ProductRequest;
import com.goodsmoa.goodsmoa_BE.product.DTO.ProductResponse;
import com.goodsmoa.goodsmoa_BE.product.Entity.ProductEntity;
import com.goodsmoa.goodsmoa_BE.product.Entity.ProductPostEntity;
import com.goodsmoa.goodsmoa_BE.product.Repository.ProductPostRepository;
import com.goodsmoa.goodsmoa_BE.product.Repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductConverter productConverter;
    private final ProductRepository productRepository;
    private final ProductPostRepository productPostRepository;

    // 상품 생성
    public ResponseEntity<ProductResponse> createProduct(ProductRequest request) {

        Optional<ProductPostEntity> ope = productPostRepository.findById(request.getPostId());
        if(ope.isEmpty()){
            return ResponseEntity.badRequest().build();
        }
        ProductPostEntity postEntity = ope.get();

        ProductEntity entity = productConverter.toEntity(postEntity, request);
        ProductEntity saveEntity = productRepository.save(entity);
        ProductResponse response = productConverter.toResponse(saveEntity);
        return ResponseEntity.ok(response);
    }

}
