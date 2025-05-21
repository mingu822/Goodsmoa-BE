package com.goodsmoa.goodsmoa_BE.demand.service;

import com.goodsmoa.goodsmoa_BE.category.Entity.Category;
import com.goodsmoa.goodsmoa_BE.category.Repository.CategoryRepository;
import com.goodsmoa.goodsmoa_BE.demand.converter.DemandPostConverter;
import com.goodsmoa.goodsmoa_BE.demand.converter.DemandPostProductConverter;
import com.goodsmoa.goodsmoa_BE.demand.dto.post.*;
import com.goodsmoa.goodsmoa_BE.demand.entity.DemandPostDocument;
import com.goodsmoa.goodsmoa_BE.demand.entity.DemandPostEntity;
import com.goodsmoa.goodsmoa_BE.demand.entity.DemandPostProductEntity;
import com.goodsmoa.goodsmoa_BE.demand.repository.DemandPostRepository;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class DemandPostService {

    @PersistenceContext
    private EntityManager entityManager;
    private final DemandPostConverter demandPostConverter;
    private final DemandPostRepository demandPostRepository;
    private final DemandPostProductConverter demandPostProductConverter;
    private final CategoryRepository categoryRepository;
    private final DemandPostSearchService demandPostSearchService;

    // 모든 글 리스트 가져오기. (비공개/공개,종료되지 않은, 삭제되지 않은)
    public List<DemandPostListResponse> getDemandEntityList(boolean state) {
        return demandPostRepository.findAllByEndTimeAfterAndStateAndDeletedAtIsNull(LocalDateTime.now(), state).stream()
                .map(demandPostConverter::toListResponse)
                .toList();
    }

    // 특정 키워드로 검색하기
//    public Page<DemandPostListResponse> searchDemandPosts(String keyword, int page) {
//        SearchHits<DemandPostDocument> searchHits = demandPostSearchService.search(keyword, page);
//
//        List<DemandPostListResponse> content = searchHits.getSearchHits().stream()
//                .map(hit -> demandPostConverter.toListResponse(
//                        findByIdWithThrow(hit.getContent().getId())
//                ))
//                .toList();
//
//        long totalHits = searchHits.getTotalHits();
//
//        return new PageImpl<>(content, PageRequest.of(page, 2), totalHits);
//    }

    public Page<DemandPostListResponse> searchDemandPosts(String keyword,
                                                          Integer category,
                                                          String sortBy,
                                                          boolean includeExpired,
                                                          boolean includeScheduled,
                                                          boolean excludePrivate,
                                                          int page) {
        SearchHits<DemandPostDocument> searchHits = demandPostSearchService.search(keyword, category, sortBy , includeExpired, includeScheduled, excludePrivate, page);

        List<DemandPostListResponse> content = searchHits.getSearchHits().stream()
                .map(hit -> demandPostConverter.toListResponse(
                        findByIdWithThrow(hit.getContent().getId())
                ))
                .toList();

        return new PageImpl<>(content, PageRequest.of(page, 2), searchHits.getTotalHits());
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
    public DemandPostResponse createDemand(UserEntity user, DemandPostCreateRequest request) {
        Category category = findCategoryByIdWithThrow(request.getCategoryId());

        DemandPostEntity postEntity = demandPostConverter.toEntity(user, category, request);
        List<DemandPostProductEntity> products = request.getProducts().stream()
                .map(product -> demandPostProductConverter.toEntity(postEntity, product))
                .toList();

        postEntity.getProducts().addAll(products);
        demandPostRepository.save(postEntity);
        demandPostSearchService.saveOrUpdateDocument(postEntity);

        return demandPostConverter.toResponse(postEntity);
    }

    // 수요조사 글 수정하기
    @Transactional
    public DemandPostResponse updateDemand(UserEntity user, DemandPostUpdateRequest request) {
        // 기존글 조회 및 수정 권한 확인
        DemandPostEntity postEntity = findByIdWithThrow(request.getId());
        validateUserAuthorization(user.getId(), postEntity);
        List<DemandPostProductEntity> products = postEntity.getProducts();

        // 기존 상품 목록을 Map 으로 변환 (ID -> 상품 객체)
        Map<Long, DemandPostProductEntity> existingProductsMap = new HashMap<>();
        for (DemandPostProductEntity product : postEntity.getProducts()) {
            existingProductsMap.put(product.getId(), product);
        }

        // 요청 받은 상품 ID 목록
        Set<Long> incomingProductIds = new HashSet<>();

        // 상품 추가 또는 수정
        for (DemandPostProductRequest productRequest : request.getProducts()) {
            Long productId = productRequest.getId();

            if (productId == null) { // 신규 상품 추가
                DemandPostProductEntity postProductEntity = demandPostProductConverter.toEntity(postEntity, productRequest);
                entityManager.persist(postProductEntity);
            } else { // 기존 상품 수정
                incomingProductIds.add(productId);
                DemandPostProductEntity existingProduct = existingProductsMap.get(productId);
                if (existingProduct == null) {
                    throw new IllegalStateException("해당 ID를 가진 상품을 찾을 수 없습니다: " + productId);
                }
                existingProduct.DemandPostProductUpdate(
                        productRequest.getPrice(),
                        productRequest.getImageUrl(),
                        productRequest.getTargetCount()
                );
            }
        }

        // 상품 삭제
        List<DemandPostProductEntity> productsToRemove = new ArrayList<>();
        for (DemandPostProductEntity product : postEntity.getProducts()) {
            // 요청 받은 상품 ID 리스트에 기존 상품이 없으면 삭제 대상
            if (!incomingProductIds.contains(product.getId())) {
                productsToRemove.add(product);
            }
        }
        products.removeAll(productsToRemove);

        // 상품 목록을 제외한 필드 업데이트
        postEntity.updateDemandEntity(
                request.getTitle(),
                request.getDescription(),
                request.getStartTime(),
                request.getEndTime(),
                request.getImageUrl(),
                request.getHashtag(),
                findCategoryByIdWithThrow(request.getCategoryId())
        );
        demandPostSearchService.saveOrUpdateDocument(postEntity);

        return demandPostConverter.toResponse(postEntity);
    }

    // 수요조사 글 삭제
    public String deleteDemand(UserEntity user, Long id){
        DemandPostEntity postEntity = findByIdWithThrow(id);
        validateUserAuthorization(user.getId(), postEntity);
        demandPostRepository.delete(postEntity);
        demandPostSearchService.deletePostDocument(id);
        return "글을 삭제하였습니다";
    }

    // 수요조사 글 끌어올리기
    public String pullDemand(UserEntity user, Long id) {
        DemandPostEntity postEntity = findByIdWithThrow(id);
        validateUserAuthorization(user.getId(), postEntity);;
        try {
            demandPostSearchService.updatePulledAt(postEntity.getId());
            return "글을 끌어올렸습니다";
        } catch (IllegalStateException e){
            return e.getMessage();
        }
    }

    // 수요조사 글 활성/비활성화
    public String stateSwitch(UserEntity user, Long id) {
        DemandPostEntity postEntity = findByIdWithThrow(id);
        validateUserAuthorization(user.getId(), postEntity);
        postEntity.toggleState();
        demandPostSearchService.updateState(postEntity.getId(), postEntity.isState());
        return postEntity.isState() ? "수요조사를 공개했습니다" : "수요조사를 비공개했습니다";
    }

    // 수요조사 글 조회(Id)
    protected DemandPostEntity findByIdWithThrow(Long id){
        return demandPostRepository.findDemandPostEntityById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 수요조사는 존재하지 않습니다"));
    }

    // 카테고리 조회
    private Category findCategoryByIdWithThrow(Integer id){
        return categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리는 존재하지 않습니다"));
    }
    // 권한 조회
    private void validateUserAuthorization(String userId, DemandPostEntity entity) {
        if (!entity.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("권한이 없습니다");
        }
    }
}
