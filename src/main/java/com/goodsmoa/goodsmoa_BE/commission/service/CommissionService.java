package com.goodsmoa.goodsmoa_BE.commission.service;

import com.goodsmoa.goodsmoa_BE.commission.converter.CommissionDetailConverter;
import com.goodsmoa.goodsmoa_BE.commission.converter.CommissionPostConverter;
import com.goodsmoa.goodsmoa_BE.commission.dto.detail.CommissionDetailRequest;
import com.goodsmoa.goodsmoa_BE.commission.dto.detail.CommissionDetailResponse;
import com.goodsmoa.goodsmoa_BE.commission.dto.post.*;
import com.goodsmoa.goodsmoa_BE.commission.entity.CommissionDetailEntity;
import com.goodsmoa.goodsmoa_BE.commission.entity.CommissionPostEntity;
import com.goodsmoa.goodsmoa_BE.commission.repository.CommissionDetailRepository;
import com.goodsmoa.goodsmoa_BE.commission.repository.CommissionRepository;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommissionService {

    private final CommissionDetailConverter commissionDetailConverter;
    private final CommissionRepository commissionRepository;
    private final CommissionDetailRepository commissionDetailRepository;
    private final CommissionPostConverter commissionPostConverter;


    /// 임시 커미션 글 생성
    public ResponseEntity<SavePostResponse> saveCommissionPost(@AuthenticationPrincipal UserEntity user, SavePostRequest request) {

        CommissionPostEntity entity = commissionPostConverter.saveToEntity(request,user);

        CommissionPostEntity savedEntity = commissionRepository.save(entity);

        SavePostResponse response = commissionPostConverter.saveToResponse(savedEntity);

        return ResponseEntity.ok(response);
    }

    /// 상세 신청 양식 만들기
    public ResponseEntity<CommissionDetailResponse> createCommissionDetail(CommissionDetailRequest request) {

        log.info("request.getCommissionId() : "+ String.valueOf(request.getCommissionId()));
        Optional<CommissionPostEntity> ope = commissionRepository.findById(request.getCommissionId());

        if(ope.isEmpty()){
            return ResponseEntity.badRequest().build();
        }

        CommissionPostEntity postEntity = ope.get();

        CommissionDetailEntity entity = commissionDetailConverter.detailToEntity(postEntity,request);
        CommissionDetailEntity saveEntity = commissionDetailRepository.save(entity);
        CommissionDetailResponse response = commissionDetailConverter.detailToResponse(saveEntity);
        return ResponseEntity.ok(response);
    }

    /// 완전한 커미션글 생성 및 수정 시 사용
    public ResponseEntity<PostResponse> updateCommissionPost(UserEntity user, PostRequest request) {

        Optional<CommissionPostEntity> oe = commissionRepository.findById(request.getId());

        if(oe.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        CommissionPostEntity entity = oe.get();

        if (!entity.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build(); // 권한 체크
        }

        entity.updateFromRequest(request,true);

        CommissionPostEntity saveEntity = commissionRepository.save(entity);

        PostResponse response = commissionPostConverter.toResponse(saveEntity);

        return ResponseEntity.ok(response);
    }

    // 커미션 글 삭제
    public ResponseEntity<String> deleteCommissionPost(UserEntity user, Long id) {
        Optional<CommissionPostEntity> oe = commissionRepository.findById(id);

        if(oe.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        CommissionPostEntity entity = oe.get();

        if (!entity.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build(); // 권한 체크
        }
        commissionRepository.delete(entity);

        return ResponseEntity.ok("삭제가 완료되었습니다.");
    }

    // 커미션 글 조회
    public ResponseEntity<PostDetailResponse> detailCommissionPost(Long id) {
        Optional<CommissionPostEntity> oe = commissionRepository.findById(id);

        if(oe.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        CommissionPostEntity entity = oe.get();

        entity.increaseViews();

        CommissionPostEntity increaseEntity = commissionRepository.save(entity);

        List<CommissionDetailEntity> detailEntities = commissionDetailRepository.findByCommissionPostEntity(increaseEntity);

        PostDetailResponse response = commissionPostConverter.detailPostToResponse(increaseEntity,detailEntities);

        return ResponseEntity.ok(response);
    }
}
