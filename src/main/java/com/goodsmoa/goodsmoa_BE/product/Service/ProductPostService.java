package com.goodsmoa.goodsmoa_BE.product.Service;

import com.goodsmoa.goodsmoa_BE.category.Entity.Category;
import com.goodsmoa.goodsmoa_BE.category.Repository.CategoryRepository;
import com.goodsmoa.goodsmoa_BE.product.Converter.ProductPostConverter;
import com.goodsmoa.goodsmoa_BE.product.DTO.Post.*;
import com.goodsmoa.goodsmoa_BE.product.Entity.ProductEntity;
import com.goodsmoa.goodsmoa_BE.product.Entity.ProductPostEntity;
import com.goodsmoa.goodsmoa_BE.product.Repository.ProductPostRepository;
import com.goodsmoa.goodsmoa_BE.product.Repository.ProductRepository;
import com.goodsmoa.goodsmoa_BE.user.Entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductPostService {

    private final ProductPostConverter productPostConverter;
    private final ProductPostRepository productPostRepository;

    private final ProductRepository productRepository;

    private final CategoryRepository categoryRepository;

    // 임시 상품글 생성
    // TODO 예외처리 추가하기
    public ResponseEntity<SavePostResponse> saveProductPost(@AuthenticationPrincipal User user, SavePostRequest request){
        ProductPostEntity entity = productPostConverter.saveToEntity(request, user);
        productPostRepository.save(entity);
        SavePostResponse response = productPostConverter.saveToResponse(entity);
        return ResponseEntity.ok(response);
    }

    // 상품글 생성
    // TODO 예외처리 추가하기
    @Transactional
    public ResponseEntity<PostResponse> createProductPost(@AuthenticationPrincipal User user, PostRequest request){
        // 임시 저장된 entity
        Optional<ProductPostEntity> oe = productPostRepository.findById(request.getId());

        //
        if(oe.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        ProductPostEntity entity = oe.get();
        if (!entity.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build(); // 권한 체크
        }

        Category category = categoryRepository.getReferenceById(request.getCategoryId());

        entity.updateCategoryAndStatus(category,true);
        entity.updateFromRequest(request);
        productPostRepository.save(entity);
        PostResponse response = productPostConverter.createToResponse(entity);
        return ResponseEntity.ok(response);
    }

    // 상품글, 상품의 정보 조회
    public ResponseEntity<PostDetailResponse> detailProductPost(Long id){
        Optional<ProductPostEntity> ope = productPostRepository.findById(id);

        if(ope.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        ProductPostEntity entity = ope.get();

        List<ProductEntity> products = productRepository.findByProductPostEntity(entity);

        PostDetailResponse response = productPostConverter.detailToResponse(products,entity);

        return ResponseEntity.ok(response);
    }
}
