package com.goodsmoa.goodsmoa_BE.product.service;

import com.goodsmoa.goodsmoa_BE.product.converter.ProductLikeConverter;
import com.goodsmoa.goodsmoa_BE.product.dto.like.ProductLikeResponse;
import com.goodsmoa.goodsmoa_BE.product.entity.ProductLikeEntity;
import com.goodsmoa.goodsmoa_BE.product.entity.ProductPostEntity;
import com.goodsmoa.goodsmoa_BE.product.repository.ProductLikeRepository;
import com.goodsmoa.goodsmoa_BE.product.repository.ProductPostRepository;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductLikeService {

    private final ProductPostRepository productPostRepository;
    private final ProductLikeRepository productLikeRepository;
    private final ProductLikeConverter productLikeConverter;

    @Transactional
    public ResponseEntity<ProductLikeResponse> likeProduct(
            UserEntity user,Long id
    ){
        ProductPostEntity entity = productPostRepository.findById(id).orElseThrow(()-> new IllegalArgumentException("해당 글이 존재하지 않습니다."));

        if(productLikeRepository.existsByProductPostEntityAndUser(entity,user)){
            throw new IllegalArgumentException("이미 찜한 상품입니다.");
        }
        ProductLikeEntity like = productLikeConverter.toEntity(entity, user);

        productLikeRepository.save(like);

        ProductLikeResponse response = productLikeConverter.toResponse(entity,user);

        return ResponseEntity.ok(response);
    }

    @Transactional
    public ResponseEntity<Void> unlikeProduct(UserEntity user, Long id) {
        ProductPostEntity entity = productPostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 거래글이 존재하지 않습니다."));

        ProductLikeEntity like = productLikeRepository.findByProductPostEntityAndUser(entity,user)
                .orElseThrow(() -> new IllegalArgumentException("해당 찜이 존재하지 않습니다."));
        productLikeRepository.delete(like);
        return ResponseEntity.ok().build();
    }

}
