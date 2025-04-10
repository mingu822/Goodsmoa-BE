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
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductPostViewService productPostViewService;

    private final ProductConverter productConverter;
    private final ProductRepository productRepository;

    private final ProductPostConverter productPostConverter;
    private final ProductPostRepository productPostRepository;

    private final ProductDeliveryConverter productDeliveryConverter;
    private final ProductDeliveryRepository productDeliveryRepository;

    private final CategoryRepository categoryRepository;

    // 상품글 생성
    @Transactional
    public ResponseEntity<PostDetailResponse> createPost(UserEntity user, PostRequest request) {
        Category category = categoryRepository.getReferenceById(request.getCategoryId());

        // ✅ 게시글 생성 및 저장
        ProductPostEntity entity = productPostConverter.createToEntity(request, user, category);
        ProductPostEntity saveEntity = productPostRepository.save(entity);

        List<ProductEntity> products = new ArrayList<>();
        
        List<ProductDeliveryEntity> delivers = new ArrayList<>();

        // 상품 추가
        if (request.getProducts() != null && !request.getProducts().isEmpty()) {
            products = request.getProducts().stream()
                    .map(productRequest -> productConverter.toEntity(saveEntity, productRequest))
                    .collect(Collectors.toList());
            productRepository.saveAll(products); // 상품 저장
        }

        // 배달방법 추가
        if (request.getDelivers() != null && !request.getDelivers().isEmpty()) {
            delivers = request.getDelivers().stream()
                    .map(productDeliveryRequest -> productDeliveryConverter.toEntity(productDeliveryRequest,saveEntity))
                    .toList();
            productDeliveryRepository.saveAll(delivers); // 배달지 저장
        }

        PostDetailResponse response = productPostConverter.detailToResponse(products,delivers,entity);

        return ResponseEntity.ok(response);
    }

    // 상품글 업데이트 -> 상품글 업데이트 시 상품과 배달 방식이 수정이 있으면 동시에 수정
    @Transactional
    public ResponseEntity<PostResponse> updateProductPost(@AuthenticationPrincipal UserEntity user, PostRequest request) {
        // 기존 게시글 가져오기
        ProductPostEntity entity = productPostRepository.findById(request.getId())
                .orElseThrow(() -> new EntityNotFoundException("해당 상품글이 존재하지 않습니다."));

        // 게시글 작성자가 맞는지 확인
        if (!entity.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build(); // 권한 체크
        }

        // 카테고리 정보 가져오기
        Category category = categoryRepository.getReferenceById(request.getCategoryId());

        // ✅ 게시글 정보 업데이트
        entity.updateFromRequest(request, category, true);

        // ✅ 상품 업데이트 로직
        // Function.identity() -> Map에 객체를 넣을 때 사용
        List<ProductEntity> products = productRepository.findByProductPostEntity(entity);
        Map<Long, ProductEntity> productMap = products.stream()
                .collect(Collectors.toMap(ProductEntity::getId, Function.identity()));

        List<ProductEntity> updatedProducts = new ArrayList<>();

        for (ProductRequest productRequest : request.getProducts()) {
            if (productRequest.getId() != null && productMap.containsKey(productRequest.getId())) {
                // 기존 상품 업데이트
                ProductEntity existingProduct = productMap.get(productRequest.getId());
                existingProduct.updateFromRequest(productRequest);
                updatedProducts.add(existingProduct);
            } else {
                // 새로운 상품 추가
                updatedProducts.add(productConverter.toEntity(entity, productRequest));
            }
        }
        productRepository.saveAll(updatedProducts);

        // 기존 상품 중 삭제된 상품 제거
        Set<Long> updatedProductIds = request.getProducts().stream()
                .map(ProductRequest::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        List<ProductEntity> deleteProducts = products.stream()
                .filter(p -> !updatedProductIds.contains(p.getId()))
                .toList();
        productRepository.deleteAll(deleteProducts);

        // ✅ 배달방법 업데이트 로직
        List<ProductDeliveryEntity> delivers = productDeliveryRepository.findByProductPostEntity(entity);

        Map<Long, ProductDeliveryEntity> deliverMap = delivers.stream()
                .collect(Collectors.toMap(ProductDeliveryEntity::getId, Function.identity()));

        List<ProductDeliveryEntity> updatedDelivers = new ArrayList<>();

        for (ProductDeliveryRequest deliverRequest : request.getDelivers()) {
            if (deliverRequest.getId() != null && deliverMap.containsKey(deliverRequest.getId())) {
                // 기존 배달방법 업데이트
                ProductDeliveryEntity existingDeliver = deliverMap.get(deliverRequest.getId());
                existingDeliver.updateFromRequest(deliverRequest);
                updatedDelivers.add(existingDeliver);
            } else {
                // 새로운 배달방법 추가
                updatedDelivers.add(productDeliveryConverter.toEntity(deliverRequest, entity));
            }
        }
        productDeliveryRepository.saveAll(updatedDelivers);

        // 기존 배달 방법 중 삭제된 항목 제거
        Set<Long> updatedDeliverIds = request.getDelivers().stream()
                .map(ProductDeliveryRequest::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        List<ProductDeliveryEntity> deleteDelivers = delivers.stream()
                .filter(d -> !updatedDeliverIds.contains(d.getId()))
                .toList();
        productDeliveryRepository.deleteAll(deleteDelivers);

        // ✅ 최신 데이터 다시 조회
        List<ProductEntity> finalProducts = productRepository.findByProductPostEntity(entity);
        List<ProductDeliveryEntity> finalDelivers = productDeliveryRepository.findByProductPostEntity(entity);

        // ✅ 게시글 저장
        productPostRepository.save(entity);

        // 응답 변환 후 반환
        PostResponse response = productPostConverter.createToResponse(entity, finalProducts, finalDelivers);
        return ResponseEntity.ok(response);

    }

    // 상품글, 상품, 배달지의 정보 조회
    public ResponseEntity<PostDetailResponse> detailProductPost(Long id){

        ProductPostEntity entity = productPostRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("해당 상품글이 존재하지 않습니다."));

        // 레디스로 조회수 증가
        productPostViewService.increaseViewCount(id);

//        // 조회수 증가
//        entity.increaseViews();
//        productPostRepository.save(entity);

        List<ProductEntity> products = productRepository.findByProductPostEntity(entity);

        List<ProductDeliveryEntity> delivers = productDeliveryRepository.findByProductPostEntity(entity);

        PostDetailResponse response = productPostConverter.detailToResponse(products,delivers,entity);

        return ResponseEntity.ok(response);
    }

    // 상품글 삭제
    public ResponseEntity<String> deleteProductPost(@AuthenticationPrincipal UserEntity user, Long id){
        
        ProductPostEntity entity = productPostRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("해당 상품글이 존재하지 않습니다."));

        // 삭제를 요청한 유저와 판매자의 유저의 정보가 일치하는지 확인
        if (!entity.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).body("본인이 아닙니다."); // 권한 체크
        }
        // 삭제
        productPostRepository.delete(entity);

        return ResponseEntity.ok("성공적으로 삭제 되었습니다.");
    }

    // 상품글 리스트
    public ResponseEntity<Page<PostsResponse>> getProductPostList(Pageable pageable) {
        Page<ProductPostEntity> postPage = productPostRepository.findAll(pageable);
        Page<PostsResponse> responsePage = postPage.map(productPostConverter::toPostsResponse); // PostResponse로 변환

        return ResponseEntity.ok(responsePage);
    }

    // TODO 필요시 사용
//    // ------------------------------------상품-----------------------------------------
//    // 상품 추가
//    public ResponseEntity<ProductResponse> createProduct(ProductRequest request) {
//
//        ProductPostEntity postEntity = productPostRepository.findById(request.getPostId()).orElseThrow(()-> new EntityNotFoundException("해당 상품글이 존재하지 않습니다."));
//
//        ProductEntity entity = productConverter.toEntity(postEntity, request);
//        ProductEntity saveEntity = productRepository.save(entity);
//        ProductResponse response = productConverter.toResponse(saveEntity);
//        return ResponseEntity.ok(response);
//    }
//
//    // 상품 수정
//    public ResponseEntity<ProductResponse> updateProduct(UserEntity user, ProductRequest request) {
//
//        ProductEntity entity = productRepository.findById(request.getId()).orElseThrow(()-> new EntityNotFoundException("존재하지 않은 상품입니다."));
//        ProductPostEntity postEntity = productPostRepository.findById(request.getPostId()).orElseThrow(()-> new EntityNotFoundException("존재하지 않은 상품글입니다."));
//
//        if(!user.getId().equals(postEntity.getUser().getId())){
//            return ResponseEntity.status(403).build(); // 권한 체크
//        }
//        productRepository.save(entity);
//        ProductResponse response = productConverter.toResponse(entity);
//
//        return ResponseEntity.ok(response);
//    }
//
//    // 상품 삭제
//    public ResponseEntity<String> deleteProduct(@AuthenticationPrincipal UserEntity user, Long id){
//
//        ProductEntity entity = productRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("존재하지 않은 상품입니다."));
//
//        // 삭제를 요청한 유저와 판매자의 유저의 정보가 일치하는지 확인
//        ProductPostEntity postEntity = productPostRepository.findById(entity.getProductPostEntity().getId()).orElseThrow(()-> new EntityNotFoundException("존재하지 않은 상품입니다."));
//
//        if(!user.getId().equals(postEntity.getUser().getId())){
//            return ResponseEntity.status(403).body("본인이 아닙니다."); // 권한 체크
//        }
//        // 삭제
//        productRepository.delete(entity);
//
//        return ResponseEntity.ok("성공적으로 삭제 되었습니다.");
//    }
//
//    // ------------------------------------배달지-----------------------------------------
//    // 배달지 추가
//    public ResponseEntity<ProductDeliveryResponse> createProductDelivery(ProductDeliveryRequest request) {
//
//        ProductPostEntity postEntity = productPostRepository.findById(request.getPostId()).orElseThrow(() -> new EntityNotFoundException("해당 상품글이 존재하지 않습니다."));
//
//        ProductDeliveryEntity entity = productDeliveryConverter.toEntity(request, postEntity);
//
//        ProductDeliveryEntity saveEntity = productDeliveryRepository.save(entity);
//
//        ProductDeliveryResponse response = productDeliveryConverter.toResponse(saveEntity);
//
//        return ResponseEntity.ok(response);
//    }
//
//    // 배송지 수정
//    public ResponseEntity<ProductDeliveryResponse> updateProductDelivery(UserEntity user, ProductDeliveryRequest request) {
//
//        ProductDeliveryEntity entity = productDeliveryRepository.findById(request.getId()).orElseThrow(()-> new EntityNotFoundException("존재하지 않은 상품입니다."));
//        ProductPostEntity postEntity = productPostRepository.findById(request.getPostId()).orElseThrow(()-> new EntityNotFoundException("존재하지 않은 상품글입니다."));
//
//        if(!user.getId().equals(postEntity.getUser().getId())){
//            return ResponseEntity.status(403).build(); // 권한 체크
//        }
//        productDeliveryRepository.save(entity);
//        ProductDeliveryResponse response = productDeliveryConverter.toResponse(entity);
//
//        return ResponseEntity.ok(response);
//    }
//
//    // 배송지 삭제
//    public ResponseEntity<String> deleteProductDelivery(@AuthenticationPrincipal UserEntity user, Long id){
//
//        ProductDeliveryEntity entity = productDeliveryRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("존재하지 않은 상품입니다."));
//
//        // 삭제를 요청한 유저와 판매자의 유저의 정보가 일치하는지 확인
//        ProductPostEntity postEntity = productPostRepository.findById(entity.getProductPostEntity().getId()).orElseThrow(()-> new EntityNotFoundException("존재하지 않은 상품입니다."));
//
//        if(!user.getId().equals(postEntity.getUser().getId())){
//            return ResponseEntity.status(403).body("본인이 아닙니다."); // 권한 체크
//        }
//        // 삭제
//        productDeliveryRepository.delete(entity);
//
//        return ResponseEntity.ok("성공적으로 삭제 되었습니다.");
//    }

}
