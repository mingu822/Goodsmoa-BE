package com.goodsmoa.goodsmoa_BE.demand.service;

import com.goodsmoa.goodsmoa_BE.demand.converter.DemandPostConverter;
import com.goodsmoa.goodsmoa_BE.demand.dto.post.DemandPostResponse;
import com.goodsmoa.goodsmoa_BE.demand.dto.post.DemandPostUpdateRequest;
import com.goodsmoa.goodsmoa_BE.demand.entity.DemandParticipateEntity;
import com.goodsmoa.goodsmoa_BE.demand.entity.DemandPostEntity;
import com.goodsmoa.goodsmoa_BE.demand.repository.DemandParticipateRepository;
import com.goodsmoa.goodsmoa_BE.demand.repository.DemandPostRepository;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DemandParticipateService {

    private final DemandPostRepository demandPostRepository;
    private final DemandPostConverter demandPostConverter;
    private final DemandParticipateRepository demandParticipateRepository;

    // 모든 글 리스트 가져오기. (비공개,종료되지 않은)
    public List<DemandPostEntity> getDemandEntityList() {
        List<DemandPostEntity> postList = new ArrayList<>();

        for(DemandPostEntity entity : demandPostRepository.findAllByEndTimeAfterAndState(LocalDateTime.now(), true)) {
            entity.getId();
        }
        return demandPostRepository.findAllByEndTimeAfterAndState(LocalDateTime.now(), true);
    }

    // 선택한 글의 id로 검색하여 가져오기
    public DemandPostResponse getDemandPostResponse(Long id) {
        DemandPostEntity postEntity = findByIdWithThrow(id);
        // 조회수 증가
        postEntity.increaseViewCount();
        return demandPostConverter.toResponse(postEntity);
    }

    // 수요조사 글 생성하기
//    @Transactional
//    public DemandPostEntity createDemand(@AuthenticationPrincipal UserEntity user, DemandPostCreateRequest request) {
//        Category category = category;
//        DemandPostEntity postEntity = demandPostRepository.save(demandPostConverter.toCreateEntity(request));
//
//        for (DemandProductEntity product : request.getProducts()) {
//            product.addDemandPost(postEntity);
//            postEntity.getProducts().add(product);
//        }
//        return postEntity;
//    }
    
    // 수요조사 글 수정하기
    @Transactional
    public DemandPostEntity updateDemand(@AuthenticationPrincipal UserEntity user, DemandPostUpdateRequest request) {
        DemandPostEntity postEntity = findByIdWithThrow(request.getId());

        // 글 작성자 ID와 수정하려는 유저 ID 비교하기
        validateUserAuthorization(user.getId(), postEntity);
        return postEntity;
    }

    // 수요조사 글 삭제하기
    public String deleteDemand(@AuthenticationPrincipal UserEntity user, Long id) {
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

    // 권한 조회
    private void validateUserAuthorization(String userId, DemandPostEntity entity) {
        if (!entity.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("권한이 없습니다");
        }
    }

    // 수요조사 참여 생성하기
//    public DemandParticipateEntity participateDemand(@AuthenticationPrincipal User user, DemandPostCreateRequest request) {
//        DemandParticipateEntity participateEntity //= demandParticipateRepository.save(demandPostConverter.toCreateEntity(request));
////        return participateEntity;
//    }


    // 소요조사 참여자 유무 확인
    private DemandParticipateEntity confirmParticipation(Long id) {
        return demandParticipateRepository.findDemandParticipateEntityById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 수요조사 참여는 존재하지 않습니다"));
    }


}
