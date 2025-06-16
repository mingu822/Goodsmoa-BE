package com.goodsmoa.goodsmoa_BE.demand.service;

import com.goodsmoa.goodsmoa_BE.category.Entity.Category;
import com.goodsmoa.goodsmoa_BE.category.Repository.CategoryRepository;
import com.goodsmoa.goodsmoa_BE.demand.converter.DemandOrderConverter;
import com.goodsmoa.goodsmoa_BE.demand.converter.DemandOrderProductConverter;
import com.goodsmoa.goodsmoa_BE.demand.dto.order.*;
import com.goodsmoa.goodsmoa_BE.demand.dto.post.DemandSearchRequest;
import com.goodsmoa.goodsmoa_BE.demand.entity.DemandOrderEntity;
import com.goodsmoa.goodsmoa_BE.demand.entity.DemandOrderProductEntity;
import com.goodsmoa.goodsmoa_BE.demand.entity.DemandPostEntity;
import com.goodsmoa.goodsmoa_BE.demand.entity.DemandPostProductEntity;
import com.goodsmoa.goodsmoa_BE.demand.repository.DemandOrderRepository;
import com.goodsmoa.goodsmoa_BE.demand.repository.DemandPostProductRepository;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DemandOrderService {

    private final DemandOrderRepository demandOrderRepository;
    private final DemandOrderConverter demandOrderConverter;
    private final DemandPostService demandPostService;
    private final DemandPostProductRepository demandPostProductRepository;
    private final DemandOrderProductConverter demandOrderProductConverter;
    private final DemandOrderCountService demandOrderCountService;
    private final CategoryRepository categoryRepository;

    // 로그인 중인 유저가 주문한 모든 수요조사
    public Page<DemandOrderResponse> getDemandOrderList(UserEntity user, DemandSearchRequest request) {
        Page<DemandOrderEntity> pageResult;
        PageRequest pageRequest = PageRequest.of(
                request.getPage(),
                request.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        pageResult = demandOrderRepository.findByUserId(
                user.getId(),
                pageRequest
        );

        return pageResult.map(demandOrderConverter::toResponse);
    }

    // 수요조사 주문 상세보기
    public DemandOrderResponse getDemandOrderResponse(Long id) {
        return demandOrderConverter.toResponse(findOrderByIdWithThrow(id));
    }

    // 수요조사 주문 생성하기
    @Transactional
    public DemandOrderResponse createDemandOrder(UserEntity user, Long id, DemandOrderCreateRequest request) {
        // 수요조사글 유무 확인
        DemandPostEntity postEntity = demandPostService.findByIdWithThrow(id);
        
        // 수요조사 주문 엔티티 생성
        DemandOrderEntity orderEntity = demandOrderConverter.toEntity(user, postEntity);

        // 수요조사 주문 상품 리스트 생성
        List<DemandOrderProductEntity> products = request.getProducts().stream()
                .map(product -> {
                    // 원본글 상품 조회
                    DemandPostProductEntity postProduct = findPostProductByIdWithThrow(product.getPostProductId());
                    // Redis 카운트 증가 (상품별)
                    demandOrderCountService.increaseOrderCount(postProduct.getId(), product.getQuantity());
                    // 주문 상품 엔티티 생성
                    return demandOrderProductConverter.toEntity(product, postProduct, orderEntity);
                })
                .toList();

        orderEntity.getDemandOrderProducts().addAll(products);
        orderEntity.setCreatedAt(LocalDateTime.now());
        demandOrderRepository.save(orderEntity);
        return demandOrderConverter.toResponse(orderEntity);
    }

    // 수요조사 주문 수정하기
    @Transactional
    public DemandOrderResponse updateDemandOrder(UserEntity user, Long id, DemandOrderUpdateRequest request) {
        // 주문의 유무 확인
        DemandOrderEntity orderEntity = findOrderByIdWithThrow(id);

        // 글 작성자 본인 확인
        validateUserAuthorization(user.getId(), orderEntity.getUser().getId());

        // 기존 주문 상품들을 ID 기준으로 맵에 저장
        Map<Long, DemandOrderProductEntity> existingProductsMap = new HashMap<>();
        for (DemandOrderProductEntity product : orderEntity.getDemandOrderProducts()) {
            existingProductsMap.put(product.getPostProductEntity().getId(), product);
        }

        // 요청 받은 상품 ID 목록
        Set<Long> incomingProductIds = new HashSet<>();

        // 주문상품 추가 또는 수정
        for (DemandOrderProductRequest productRequest : request.getProducts()) {
            Long productId = productRequest.getPostProductId();
            int requestedQuantity = productRequest.getQuantity();
            incomingProductIds.add(productId);

            DemandOrderProductEntity existingProduct = existingProductsMap.get(productId);
            if (existingProduct == null) { // 신규 주문상품 추가
                DemandPostProductEntity postProductEntity = findPostProductByIdWithThrow(productId);
                DemandOrderProductEntity newProduct = demandOrderProductConverter.toEntity(productRequest, postProductEntity, orderEntity);
                orderEntity.getDemandOrderProducts().add(newProduct);
                demandOrderCountService.increaseOrderCount(productId, requestedQuantity);
            } else { // 기존 주문상품 수정
                int oldQuantity = existingProduct.getQuantity();
                int diff = requestedQuantity - oldQuantity;
                existingProduct.updateQuantity(productRequest.getQuantity());

                if (diff > 0) {
                    demandOrderCountService.increaseOrderCount(productId, diff);
                } else if (diff < 0) {
                    demandOrderCountService.decreaseOrderCount(productId, -diff);
                }
            }
        }

        // 주문상품 삭제
        List<DemandOrderProductEntity> productsToRemove = new ArrayList<>();
        for (DemandOrderProductEntity product : orderEntity.getDemandOrderProducts()) {
            // 요청 받은 상품 ID 리스트에 기존 상품이 없으면 삭제 대상
            if (!incomingProductIds.contains(product.getPostProductEntity().getId())) {
                productsToRemove.add(product);
                demandOrderCountService.decreaseOrderCount(product.getPostProductEntity().getId(), product.getQuantity());
            }
        }
        orderEntity.getDemandOrderProducts().removeAll(productsToRemove);

        return demandOrderConverter.toResponse(orderEntity);
    }

    // 수요조사 주문 삭제하기
    @Transactional
    public String deleteDemandOrder(UserEntity user, Long id) {
        DemandOrderEntity entity = findOrderByIdWithThrow(id);
        validateUserAuthorization(user.getId(), entity.getUser().getId());
        List<DemandOrderProductEntity> list = entity.getDemandOrderProducts();
        for(DemandOrderProductEntity product : list) {
            demandOrderCountService.decreaseOrderCount(
                    product.getPostProductEntity().getId(),
                    product.getQuantity()
            );
        }
        demandOrderRepository.delete(entity);
        return "주문을 삭제하였습니다";
    }

    // 권한 조회
    private void validateUserAuthorization(String loggedInUserId, String postAuthorId) {
        if (!loggedInUserId.equals(postAuthorId)) {
            throw new AccessDeniedException("권한이 없습니다");
        }
    }

    // 수요조사 주문 상세 조회
    private DemandOrderEntity findOrderByIdWithThrow(Long id) {
        return demandOrderRepository.findDemandOrderEntitiesById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 수요조사 참여는 존재하지 않습니다"));
    }

    // 본글 제품 유무 확인
    private DemandPostProductEntity findPostProductByIdWithThrow(Long id){
        return demandPostProductRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 제품은 존재하지 않습니다"));
    }

    // 카테고리 조회
    private Category findCategoryByIdWithThrow(Integer id){
        return categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리는 존재하지 않습니다"));
    }
}
