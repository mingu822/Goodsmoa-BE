package com.goodsmoa.goodsmoa_BE.product.service;

import com.goodsmoa.goodsmoa_BE.category.Entity.Category;
import com.goodsmoa.goodsmoa_BE.category.Repository.CategoryRepository;
import com.goodsmoa.goodsmoa_BE.product.converter.ProductConverter;
import com.goodsmoa.goodsmoa_BE.product.converter.ProductDeliveryConverter;
import com.goodsmoa.goodsmoa_BE.product.converter.ProductPostConverter;
import com.goodsmoa.goodsmoa_BE.product.dto.Delivery.ProductDeliveryRequest;
import com.goodsmoa.goodsmoa_BE.product.dto.Delivery.ProductDeliveryResponse;
import com.goodsmoa.goodsmoa_BE.product.dto.Post.*;
import com.goodsmoa.goodsmoa_BE.product.dto.ProductRequest;
import com.goodsmoa.goodsmoa_BE.product.dto.ProductResponse;
import com.goodsmoa.goodsmoa_BE.product.entity.ProductDeliveryEntity;
import com.goodsmoa.goodsmoa_BE.product.entity.ProductEntity;
import com.goodsmoa.goodsmoa_BE.product.entity.ProductPostEntity;
import com.goodsmoa.goodsmoa_BE.product.repository.ProductDeliveryRepository;
import com.goodsmoa.goodsmoa_BE.product.repository.ProductPostRepository;
import com.goodsmoa.goodsmoa_BE.product.repository.ProductRepository;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductConverter productConverter;
    private final ProductRepository productRepository;

    private final ProductPostConverter productPostConverter;
    private final ProductPostRepository productPostRepository;

    private final ProductDeliveryConverter productDeliveryConverter;
    private final ProductDeliveryRepository productDeliveryRepository;

    private final CategoryRepository categoryRepository;

    // 상품 생성
    public ResponseEntity<ProductResponse> createProduct(ProductRequest request) {

        // 요청받은 상품글 id를 검색
        Optional<ProductPostEntity> ope = productPostRepository.findById(request.getPostId());
        if(ope.isEmpty()){
            return ResponseEntity.badRequest().build();
        }
        // 있을경우 빼냄
        ProductPostEntity postEntity = ope.get();

        ProductEntity entity = productConverter.toEntity(postEntity, request);
        ProductEntity saveEntity = productRepository.save(entity);
        ProductResponse response = productConverter.toResponse(saveEntity);
        return ResponseEntity.ok(response);
    }

    /// 임시 상품글 생성
    // TODO 예외처리 추가하기
    public ResponseEntity<SavePostResponse> saveProductPost(@AuthenticationPrincipal UserEntity user, SavePostRequest request){
        Category category = categoryRepository.getReferenceById(request.getCategoryId());

        ProductPostEntity entity = productPostConverter.saveToEntity(request, user, category);

        productPostRepository.save(entity);
        SavePostResponse response = productPostConverter.saveToResponse(entity);
        return ResponseEntity.ok(response);
    }

    /// 상품글 생성
    // TODO 예외처리 추가하기
    @Transactional
    public ResponseEntity<PostResponse> updateProductPost(@AuthenticationPrincipal UserEntity user, PostRequest request){
        // 임시 저장된 entity
        Optional<ProductPostEntity> oe = productPostRepository.findById(request.getId());

        if(oe.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        ProductPostEntity entity = oe.get();
        if (!entity.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build(); // 권한 체크
        }

        Category category = categoryRepository.getReferenceById(request.getCategoryId());

        // 새로운 값으로 저장
        entity.updateFromRequest(request,category,true);

        productPostRepository.save(entity);

        PostResponse response = productPostConverter.createToResponse(entity);

        return ResponseEntity.ok(response);
    }

    /// 상품글, 상품, 배달지의 정보 조회
    public ResponseEntity<PostDetailResponse> detailProductPost(Long id){
        Optional<ProductPostEntity> ope = productPostRepository.findById(id);

        if(ope.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        ProductPostEntity entity = ope.get();

        // 조회수 증가
        entity.increaseViews();
        productPostRepository.save(entity);

        List<ProductEntity> products = productRepository.findByProductPostEntity(entity);

        List<ProductDeliveryEntity> delivers = productDeliveryRepository.findByProductPostEntity(entity);

        PostDetailResponse response = productPostConverter.detailToResponse(products,delivers,entity);

        return ResponseEntity.ok(response);
    }
    // 배달지 추가
    public ResponseEntity<ProductDeliveryResponse> createProductDelivery(ProductDeliveryRequest request) {

        Optional<ProductPostEntity> ope = productPostRepository.findById(request.getPostId());
        if(ope.isEmpty()){
            return ResponseEntity.badRequest().build();
        }
        ProductPostEntity postEntity = ope.get();

        ProductDeliveryEntity entity = productDeliveryConverter.toEntity(request, postEntity);

        ProductDeliveryEntity saveEntity = productDeliveryRepository.save(entity);

        ProductDeliveryResponse response = productDeliveryConverter.toResponse(saveEntity);

        return ResponseEntity.ok(response);
    }

    // 상품글 삭제
    public ResponseEntity<String> deleteProductPost(@AuthenticationPrincipal UserEntity user, Long id){

        Optional<ProductPostEntity> ope = productPostRepository.findById(id);

        // 요청한 id에 해당하는 상품글이 존재하는지 확인
        if(ope.isEmpty()){
            return ResponseEntity.badRequest().build();
        }
        ProductPostEntity entity = ope.get();

        // 삭제를 요청한 유저와 판매자의 유저의 정보가 일치하는지 확인
        if (!entity.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build(); // 권한 체크
        }
        // 삭제
        productPostRepository.delete(entity);

        return ResponseEntity.ok("성공적으로 삭제 되었습니다.");
    }
}
