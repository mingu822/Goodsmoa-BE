package com.goodsmoa.goodsmoa_BE.commission.service;

import com.goodsmoa.goodsmoa_BE.category.Entity.Category;
import com.goodsmoa.goodsmoa_BE.category.Repository.CategoryRepository;
import com.goodsmoa.goodsmoa_BE.commission.converter.CommissionDetailConverter;
import com.goodsmoa.goodsmoa_BE.commission.converter.CommissionPostConverter;
import com.goodsmoa.goodsmoa_BE.commission.dto.detail.CommissionDetailRequest;
import com.goodsmoa.goodsmoa_BE.commission.dto.detail.CommissionDetailResponse;
import com.goodsmoa.goodsmoa_BE.commission.dto.post.*;
import com.goodsmoa.goodsmoa_BE.commission.entity.CommissionDetailEntity;
import com.goodsmoa.goodsmoa_BE.commission.entity.CommissionPostEntity;
import com.goodsmoa.goodsmoa_BE.commission.repository.CommissionDetailRepository;
import com.goodsmoa.goodsmoa_BE.commission.repository.CommissionRepository;
import com.goodsmoa.goodsmoa_BE.config.S3Uploader;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommissionService {

    private final CommissionDetailConverter commissionDetailConverter;
    private final CommissionRepository commissionRepository;

    private final CommissionDetailRepository commissionDetailRepository;

    private final CommissionPostConverter commissionPostConverter;

    private final CategoryRepository categoryRepository;

    private final S3Uploader s3Uploader;

    // 커미션 글 생성
    @Transactional
    public ResponseEntity<PostResponse> createCommissionDetail(
            UserEntity user,
            PostRequest request,
            MultipartFile thumbnailImage,
            List<MultipartFile> contentImage) throws IOException {

        Category category = categoryRepository.getReferenceById(request.getCategoryId());

        CommissionPostEntity entity = commissionPostConverter.saveToEntity(request, user, category);
        CommissionPostEntity saveEntity = commissionRepository.save(entity);

        String thumbnailPath = s3Uploader.upload(thumbnailImage);
        saveEntity.setThumbnailImage(thumbnailPath);
        commissionRepository.save(saveEntity);

        // 상세 설명 이미지 저장
        if(contentImage != null && !contentImage.isEmpty()) {
            List<String> contentImagePath = new ArrayList<>();
            for(MultipartFile file : contentImage) {
                String path = s3Uploader.upload(file);
                contentImagePath.add(path);
            }
            String originalContent = request.getContent();

            // HTML의 <img src="..."> 경로를 정규식으로 찾음
            Pattern pattern = Pattern.compile("src=[\"'](.*?)[\"']");
            Matcher matcher = pattern.matcher(originalContent);

            int i = 0;
            StringBuffer result = new StringBuffer();

            while (matcher.find() && i < contentImagePath.size()) {
                String newPath = "src='" + contentImagePath.get(i++) + "'"; // 새 이미지 경로로 교체
                matcher.appendReplacement(result, newPath);
            }
            matcher.appendTail(result);

            saveEntity.setContent(result.toString());
            commissionRepository.save(saveEntity);
        }

        // 추가 설명 저장
        if(request.getDetails() != null && !request.getDetails().isEmpty()){
            for(CommissionDetailRequest requestDetail : request.getDetails()){
                CommissionDetailEntity detailEntity = commissionDetailConverter.detailToEntity(saveEntity, requestDetail);
                commissionDetailRepository.save(detailEntity);
            }
        }

        PostResponse response = commissionPostConverter.toResponse(saveEntity);

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
