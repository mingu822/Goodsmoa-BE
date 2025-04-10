package com.goodsmoa.goodsmoa_BE.demand.service;

import com.goodsmoa.goodsmoa_BE.category.Entity.Category;
import com.goodsmoa.goodsmoa_BE.category.Repository.CategoryRepository;
import com.goodsmoa.goodsmoa_BE.demand.converter.DemandPostConverter;
import com.goodsmoa.goodsmoa_BE.demand.converter.DemandPostProductConverter;
import com.goodsmoa.goodsmoa_BE.demand.dto.post.*;
import com.goodsmoa.goodsmoa_BE.demand.entity.DemandPostEntity;
import com.goodsmoa.goodsmoa_BE.demand.entity.DemandPostProductEntity;
import com.goodsmoa.goodsmoa_BE.demand.repository.DemandOrderProductRepository;
import com.goodsmoa.goodsmoa_BE.demand.repository.DemandPostRepository;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DemandPostService {

    private final DemandPostConverter demandPostConverter;
    private final DemandPostRepository demandPostRepository;
    private final DemandPostProductConverter demandPostProductConverter;
    private final DemandOrderProductRepository demandOrderProductRepository;
    private final CategoryRepository categoryRepository;

    // 모든 글 리스트 가져오기. (비공개/공개,종료되지 않은)(필요한 내용만)
    public List<DemandPostListResponse> getDemandEntityList(boolean state) {
        return demandPostRepository.findAllByEndTimeAfterAndState(LocalDateTime.now(), state).stream()
                .map(demandPostConverter::toListResponse)
                .collect(Collectors.toList());
    }

    // 선택한 글의 id로 검색하여 가져오기
    public DemandPostResponse getDemandPostResponse(Long id) {
        DemandPostEntity postEntity = findByIdWithThrow(id);
        // 조회수 증가
        postEntity.increaseViewCount();
        demandPostRepository.save(postEntity);

        return demandPostConverter.toResponse(postEntity);
    }

    // 수요조사 글 생성하기
    @Transactional
    public DemandPostResponse createDemand(@AuthenticationPrincipal UserEntity user, DemandPostCreateRequest request) {
        Category category = findCategoryByIdWithThrow(request.getCategoryId());

        DemandPostEntity postEntity = demandPostConverter.toEntity(user, category, request);
        List<DemandPostProductEntity> products = request.getProducts().stream()
                .map(product -> demandPostProductConverter.toEntity(postEntity, product))
                .toList();

        postEntity.getProducts().addAll(products);
        demandPostRepository.save(postEntity);
        return demandPostConverter.toResponse(postEntity);
    }

    // 수요조사 글 수정하기
    @Transactional
    public DemandPostResponse updateDemand(@AuthenticationPrincipal UserEntity user, DemandPostUpdateRequest request) {
        // 기존글 조회 및 수정 권한 확인
        DemandPostEntity postEntity = findByIdWithThrow(request.getId());
        validateUserAuthorization(user.getId(), postEntity);

        // 기존 상품 목록을 Map 으로 변환 (ID -> 상품 객체)
        Map<Long, DemandPostProductEntity> existingProductsMap = new HashMap<>();
        for (DemandPostProductEntity product : postEntity.getProducts()) {
            existingProductsMap.put(product.getId(), product);
        }

        // 상품 수정 및 추가
        List<DemandPostProductEntity> updatedProducts = new ArrayList<>();
        for (DemandPostProductRequest productRequest : request.getProducts()) {
            Long productId = productRequest.getId();

            if (productId == null) {
                // ID가 없으면 신규 상품으로 추가
                DemandPostProductEntity newProduct = demandPostProductConverter.toEntity(postEntity, productRequest);
                updatedProducts.add(newProduct);
            } else if (existingProductsMap.containsKey(productId)) {
                // 기존 상품이면 정보 업데이트
                DemandPostProductEntity existingProduct = existingProductsMap.get(productId);
                existingProduct.DemandPostProductUpdate(
                        productRequest.getName(),
                        productRequest.getPrice(),
                        productRequest.getImage(),
                        productRequest.getTargetCount()
                );
                updatedProducts.add(existingProduct);
            } else {
                throw new IllegalStateException("해당 ID를 가진 상품을 찾을 수 없습니다: " + productId);
            }
        }

        // 기존 상품 목록 초기화 후 변경
        postEntity.getProducts().clear();
        postEntity.getProducts().addAll(updatedProducts);

        // 상품 목록을 제외한 필드 업데이트
        postEntity.updateDemandEntity(
                request.getTitle(),
                request.getDescription(),
                request.getStartTime(),
                request.getEndTime(),
                request.getImage(),
                request.getHashtag(),
                findCategoryByIdWithThrow(request.getCategoryId())
        );
        return demandPostConverter.toResponse(postEntity);
    }

    // 수요조사 글 삭제하기
    public String deleteDemand(UserEntity user, Long id) {
        DemandPostEntity postEntity = findByIdWithThrow(id);
        validateUserAuthorization(user.getId(), postEntity);
        demandPostRepository.delete(postEntity);
        return "글을 삭제하였습니다";
    }

    public String pullDemand(UserEntity user, Long id) {
        DemandPostEntity postEntity = findByIdWithThrow(id);
        validateUserAuthorization(user.getId(), postEntity);
        postEntity.pull();
        return "글을 끌어올렸습니다";
    }

    public String stateSwitch(UserEntity user, Long id) {
        DemandPostEntity postEntity = findByIdWithThrow(id);
        validateUserAuthorization(user.getId(), postEntity);
        return postEntity.toggleState() ? "글을 공개처리했습니다" : "글을 비공개처리했습니다";
    }

    // 수요조사 글 조회(Id)
    protected DemandPostEntity findByIdWithThrow(Long id){
        return demandPostRepository.findDemandPostEntityById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 수요조사는 존재하지 않습니다"));
    }

    // 카테고리 조회
    private Category findCategoryByIdWithThrow(Integer id){
        return categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
    }

    private boolean existDemandOrderProduct(Long id){
        return demandOrderProductRepository.existsById(id);
    }

    // 권한 조회
    private void validateUserAuthorization(String userId, DemandPostEntity entity) {
        if (!entity.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("권한이 없습니다");
        }
    }
}
