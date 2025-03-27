package com.goodsmoa.goodsmoa_BE.demand.service;

import com.goodsmoa.goodsmoa_BE.category.Entity.Category;
import com.goodsmoa.goodsmoa_BE.category.Repository.CategoryRepository;
import com.goodsmoa.goodsmoa_BE.demand.converter.DemandPostConverter;
import com.goodsmoa.goodsmoa_BE.demand.converter.DemandProductConverter;
import com.goodsmoa.goodsmoa_BE.demand.dto.post.DemandPostCreateRequest;
import com.goodsmoa.goodsmoa_BE.demand.dto.post.DemandPostListResponse;
import com.goodsmoa.goodsmoa_BE.demand.dto.post.DemandPostResponse;
import com.goodsmoa.goodsmoa_BE.demand.dto.post.DemandPostUpdateRequest;
import com.goodsmoa.goodsmoa_BE.demand.entity.DemandPostEntity;
import com.goodsmoa.goodsmoa_BE.demand.entity.DemandProductEntity;
import com.goodsmoa.goodsmoa_BE.demand.repository.DemandPostRepository;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DemandPostService {

    private final DemandPostRepository demandPostRepository;
    private final CategoryRepository categoryRepository;
    private final DemandPostConverter demandPostConverter;
    private final DemandProductConverter demandProductConverter;

    // 모든 글 리스트 가져오기. (비공개,종료되지 않은)(필요한 내용만)
    public List<DemandPostListResponse> getDemandEntityList() {
        return demandPostRepository.findAllByEndTimeAfterAndState(LocalDateTime.now(), true).stream()
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
    public DemandPostResponse createDemand(UserEntity user, DemandPostCreateRequest request) {
        Category category = findCategoryByIdWithThrow(request.getCategoryId());

        DemandPostEntity postEntity = demandPostConverter.toEntity(user, category, request);
        List<DemandProductEntity> products = request.getProducts().stream()
                .map(product -> demandProductConverter.toEntity(postEntity, product))
                .toList();

        postEntity.getProducts().addAll(products);
        demandPostRepository.save(postEntity);
        return demandPostConverter.toResponse(postEntity);
    }

    // 수요조사 글 수정하기
    @Transactional
    public DemandPostResponse updateDemand(UserEntity user, DemandPostUpdateRequest request) {
        DemandPostEntity postEntity = findByIdWithThrow(request.getId());

        // 글 작성자 ID와 수정하려는 유저 ID 비교하기
        validateUserAuthorization(user.getId(), postEntity);

        // TODO: 참여인원이 있을 경우 수정 불가 조건 추가 필요.
        // TODO: (선행)수요조사 참여 교차테이블을 만들어야함
//        if(has){}

        // 기존 상품 목록 초기화 후 변경
        postEntity.getProducts().clear();
        List<DemandProductEntity> products = request.getProducts().stream()
                .map(product -> demandProductConverter.toEntity(postEntity, product))
                .toList();

        postEntity.getProducts().addAll(products);
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



    // 수요조사 글 DB 에서 조회
    private DemandPostEntity findByIdWithThrow(Long id){
        return demandPostRepository.findDemandPostEntitiesById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 수요조사는 존재하지 않습니다"));
    }

    // 카테고리 DB 에서 조회
    private Category findCategoryByIdWithThrow(Integer id){
        return categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
    }

    // 권한 조회
    private void validateUserAuthorization(String userId, DemandPostEntity entity) {
        if (!entity.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("권한이 없습니다");
        }
    }
}
