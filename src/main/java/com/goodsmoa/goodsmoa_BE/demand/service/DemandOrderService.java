package com.goodsmoa.goodsmoa_BE.demand.service;

import com.goodsmoa.goodsmoa_BE.demand.converter.DemandOrderConverter;
import com.goodsmoa.goodsmoa_BE.demand.converter.DemandOrderProductConverter;
import com.goodsmoa.goodsmoa_BE.demand.dto.order.*;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DemandOrderService {

    private final DemandOrderRepository demandOrderRepository;
    private final DemandOrderConverter demandOrderConverter;
    private final DemandPostService demandPostService;
    private final DemandPostProductRepository demandPostProductRepository;
    private final DemandOrderProductConverter demandOrderProductConverter;

    // 로그인 중인 유저가 주문한 모든 수요조사
    public List<DemandOrderEntity> getDemandOrderList(@AuthenticationPrincipal UserEntity user) {
        return demandOrderRepository.findDemandOrderEntityByUserId(user.getId());
    }

    // 수요조사 주문 상세보기
    public DemandOrderEntity getDemandOrderResponse(Long id) {
        return findOrderByIdWithThrow(id);
    }

    // 수요조사 주문 생성하기
    public DemandOrderResponse createDemandOrder(@AuthenticationPrincipal UserEntity user, DemandOrderCreateRequest request) {
        // 수요조사글 유무 확인
        DemandPostEntity postEntity = demandPostService.findByIdWithThrow(request.getDemandPostId());
        
        // 수요조사 주문 엔티티 생성
        DemandOrderEntity orderEntity = demandOrderConverter.toEntity(user, postEntity);

        // 수요조사 주문 상품 리스트 생성
        List<DemandOrderProductEntity> products = request.getProducts().stream()
                .map(product -> demandOrderProductConverter.toEntity(orderEntity, findPostProductByIdWithThrow(product.getPostProductId()),product))
                .toList();

        orderEntity.getDemandOrderProducts().addAll(products);
        demandOrderRepository.save(orderEntity);
        return demandOrderConverter.toResponse(orderEntity);
    }

    // 수요조사 주문 수정하기
    @Transactional
    public DemandOrderResponse updateDemandOrder(@AuthenticationPrincipal UserEntity user, DemandOrderUpdateRequest request) {
        // 주문의 유무 확인
        DemandOrderEntity orderEntity = findOrderByIdWithThrow(request.getOrderEntityId());

        // 글 작성자 ID와 수정하려는 유저 ID 비교하기
        validateUserAuthorization(user.getId(), orderEntity.getUser().getId());

        // 기존 주문 상품들을 ID 기준으로 맵에 저장
        Map<Long, DemandOrderProductEntity> existingProductsMap = orderEntity.getDemandOrderProducts().stream()
                .collect(Collectors.toMap(p -> p.getPostProductEntity().getId(), p -> p));

        // 요청된 상품 ID 들을 Set 에 저장
        Set<Long> requestedProductIds = request.getProducts().stream()
                .map(DemandOrderProductRequest::getPostProductId)
                .collect(Collectors.toSet());

        for (DemandOrderProductRequest productRequest : request.getProducts()) {
            Long postProductId = productRequest.getPostProductId(); // 본글 상품 ID

            if (existingProductsMap.containsKey(postProductId)) {
                // 원글의 해당 제품 ID로 맵에서 찾아서 업데이트
                DemandOrderProductEntity existingProduct = existingProductsMap.get(postProductId);
                existingProduct.updateQuantity(productRequest.getQuantity()); // 수량 업데이트
            }
            else {
                // 기존 주문 상품에 없었다면 새로 추가
                DemandPostProductEntity postProductEntity = findPostProductByIdWithThrow(productRequest.getPostProductId());
                DemandOrderProductEntity entity = demandOrderProductConverter.toEntity(orderEntity, postProductEntity, productRequest);
                orderEntity.getDemandOrderProducts().add(entity);
            }
        }
        // removeIf 는 Hibernate 변경감지 우회 가능성이 있어 사용하면 안됨
        Iterator<DemandOrderProductEntity> iterator = orderEntity.getDemandOrderProducts().iterator();
        while (iterator.hasNext()) {
            DemandOrderProductEntity product = iterator.next();
            if (!requestedProductIds.contains(product.getPostProductEntity().getId())) {
                iterator.remove();
            }
        }
        return demandOrderConverter.toResponse(orderEntity);
    }

    // 수요조사 참여 삭제하기
    @Transactional
    public String deleteDemandOrder(@AuthenticationPrincipal UserEntity user, Long id) {
        DemandOrderEntity entity = findOrderByIdWithThrow(id);
        validateUserAuthorization(user.getId(), entity.getUser().getId());
        demandOrderRepository.delete(entity);
        return "글을 삭제하였습니다";
    }

    // 권한 조회
    private void validateUserAuthorization(String loggedInUserId, String postAuthorId) {
        if (!loggedInUserId.equals(postAuthorId)) {
            throw new AccessDeniedException("권한이 없습니다");
        }
    }

    // 수요조사 참여 조회
    private DemandOrderEntity findOrderByIdWithThrow(Long id) {
        return demandOrderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 수요조사 참여는 존재하지 않습니다"));
    }

    // 본글 제품 유무 확인
    private DemandPostProductEntity findPostProductByIdWithThrow(Long id){
        return demandPostProductRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 제품은 존재하지 않습니다"));
    }
}
