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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ProductLikeService {

    private final ProductPostRepository productPostRepository;
    private final ProductLikeRepository productLikeRepository;
    private final ProductLikeConverter productLikeConverter;
    private final ProductRedisService productRedisService;

    // 찜 추가
    @Transactional
    public ResponseEntity<ProductLikeResponse> likeProduct(UserEntity user, Long id) {
        ProductPostEntity entity = productPostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 글이 존재하지 않습니다."));

        if (productLikeRepository.existsByProductPostEntityAndUser(entity, user)) {
            throw new IllegalArgumentException("이미 찜한 상품입니다.");
        }

        ProductLikeEntity like = productLikeConverter.toEntity(entity, user);
        ProductLikeEntity saveEntity = productLikeRepository.save(like);

        //  Redis에 좋아요수 반영 추가
        productRedisService.increaseLikeCount(id);

        ProductLikeResponse response = productLikeConverter.toResponse(saveEntity);
        return ResponseEntity.ok(response);
    }

    // 찜 취소
    @Transactional
    public ResponseEntity<Void> unlikeProduct(UserEntity user, Long id) {
        ProductPostEntity entity = productPostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 거래글이 존재하지 않습니다."));

        ProductLikeEntity like = productLikeRepository.findByProductPostEntityAndUser(entity,user)
                .orElseThrow(() -> new IllegalArgumentException("해당 찜이 존재하지 않습니다."));
        productLikeRepository.delete(like);

        // Redis 좋아요수 감소 반영
        productRedisService.decreaseLikeCount(id);

        return ResponseEntity.ok().build();
    }

    // 찜 리스트로 가져오기
    public ResponseEntity<Page<ProductLikeResponse>> getPagedLiked(UserEntity user, Pageable pageable) {
        Page<ProductLikeEntity> likePage = productLikeRepository.findByUser(user, pageable);

        Page<ProductLikeResponse> responses = likePage.map(productLikeConverter::toResponse);
        return ResponseEntity.ok(responses);
    }

    // 한 개의 찜 정보 가져오기
    public ProductLikeResponse getSingleLiked(UserEntity user, Long id) {
        ProductPostEntity entity = productPostRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 거래글이 존재하지 않습니다."));

        ProductLikeEntity like = productLikeRepository.findByProductPostEntityAndUser(entity, user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 찜이 존재하지 않습니다."));

        return productLikeConverter.toResponse(like);
    }
}
