package com.goodsmoa.goodsmoa_BE.commission.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goodsmoa.goodsmoa_BE.category.Entity.Category;
import com.goodsmoa.goodsmoa_BE.category.Repository.CategoryRepository;
import com.goodsmoa.goodsmoa_BE.commission.converter.CommissionDetailConverter;
import com.goodsmoa.goodsmoa_BE.commission.converter.CommissionPostConverter;
import com.goodsmoa.goodsmoa_BE.commission.dto.detail.CommissionDetailRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.goodsmoa.goodsmoa_BE.commission.dto.post.*;
import com.goodsmoa.goodsmoa_BE.commission.entity.CommissionDetailEntity;
import com.goodsmoa.goodsmoa_BE.commission.entity.CommissionPostEntity;
import com.goodsmoa.goodsmoa_BE.commission.repository.CommissionDetailRepository;
import com.goodsmoa.goodsmoa_BE.commission.repository.CommissionRepository;
import com.goodsmoa.goodsmoa_BE.config.S3Uploader;
import com.goodsmoa.goodsmoa_BE.search.service.SearchService;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    private final CommissionRedisService commissionRedisService;

    private final SearchService searchService;

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

        searchService.saveOrUpdateDocument(saveEntity);

        PostResponse response = commissionPostConverter.toResponse(saveEntity);

        return ResponseEntity.ok(response);
    }

    // 커미션 업데이트
    public ResponseEntity<PostResponse> updateCommissionPost(
            UserEntity user,
            PostRequest request,
            MultipartFile newThumbnailImage,
            List<MultipartFile> newContentImage,
            String deleteDetailIdsJson) throws IOException {

        List<Long> deleteDetailIds = new ArrayList<>();
        if (deleteDetailIdsJson != null && !deleteDetailIdsJson.isBlank()) {
            try {
                deleteDetailIds = new ObjectMapper().readValue(
                        deleteDetailIdsJson,
                        new TypeReference<>() {}
                );
            } catch (Exception e) {
                throw new RuntimeException("deleteDetailIdsJson 파싱 실패: " + deleteDetailIdsJson, e);
            }
        }

        CommissionPostEntity entity = commissionRepository.findById(request.getId()).orElseThrow(() -> new EntityNotFoundException("게시물이 존재하지 않습니다."));

        // 권한 체크
        if (!entity.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        // 새로운 썸네일 이미지가 있을 경우
        if(newThumbnailImage != null && !newThumbnailImage.isEmpty()) {
            String thumbnailPath = s3Uploader.upload(newThumbnailImage);
            entity.setThumbnailImage(thumbnailPath);
        }

        // 새로운 상세 내용 이미지가 있을 경우
        if(newContentImage != null && !newContentImage.isEmpty()) {
            List<String> newContentPaths = new ArrayList<>();
            for(MultipartFile image : newContentImage) {
                String uploadPath = s3Uploader.upload(image);
                newContentPaths.add(uploadPath);
            }

            String newContent = request.getContent();

            Pattern pattern = Pattern.compile("src=[\"'](.*?)[\"']");
            Matcher matcher = pattern.matcher(newContent);

            int i = 0;
            StringBuilder result = new StringBuilder();

            while (matcher.find() && i < newContentPaths.size()) {
                String newPath = "src='" + newContentPaths.get(i++) + "'";
                matcher.appendReplacement(result, newPath);
            }
            matcher.appendTail(result);

            entity.setContent(result.toString());
            commissionRepository.save(entity);
        }else{
            entity.setContent(request.getContent());
            commissionRepository.save(entity);
        }

        // request.getDetails() 로 넘어온 리스트 기반으로 업데이트 및 신규 생성
        if (request.getDetails() != null) {
            for (CommissionDetailRequest detailRequest : request.getDetails()) {
                if (detailRequest.getId() != null) {
                    // 기존 상세 내용 수정
                    CommissionDetailEntity detailEntity = commissionDetailRepository.findById(detailRequest.getId())
                            .orElseThrow(() -> new EntityNotFoundException("상세 요청이 존재하지 않습니다."));
                    detailEntity.setTitle(detailRequest.getTitle());
                    detailEntity.setReqContent(detailRequest.getReqContent());
                    commissionDetailRepository.save(detailEntity);
                } else {
                    // 새로운 상세 내용 추가
                    CommissionDetailEntity newDetail = commissionDetailConverter.detailToEntity(entity,detailRequest);
                    commissionDetailRepository.save(newDetail);
                }
            }
        }

        // 삭제할 상세 아이디가 있으면 삭제
        if(deleteDetailIds != null && !deleteDetailIds.isEmpty()){
            for(Long id : deleteDetailIds){
                commissionDetailRepository.deleteById(id);
            }
        }

        entity.updateFromRequest(request,true);

        CommissionPostEntity saveEntity = commissionRepository.save(entity);

        // 재색인
        searchService.saveOrUpdateDocument(saveEntity);

        PostResponse response = commissionPostConverter.toResponse(saveEntity);

        return ResponseEntity.ok(response);
    }

    // 커미션 글 삭제
    public ResponseEntity<String> deleteCommissionPost(UserEntity user, Long id) {
        CommissionPostEntity entity = commissionRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("게시물이 존재하지 않습니다."));

        if (!entity.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build(); // 권한 체크
        }

        // 관련된 디테일 불러오기
        List<CommissionDetailEntity> detailEntities = commissionDetailRepository.findByCommissionPostEntity(entity);

        // for 문으로 디테일 삭제
        for(CommissionDetailEntity detailEntity : detailEntities){
            commissionDetailRepository.deleteById(detailEntity.getId());
        }

        searchService.deletePostDocument("COMMISION_"+id);

        // 커미션 글 삭제
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

        // entity.increaseViews();
        commissionRedisService.increaseViewCount(id);


        List<CommissionDetailEntity> detailEntities = commissionDetailRepository.findByCommissionPostEntity(entity);

        PostDetailResponse response = commissionPostConverter.detailPostToResponse(entity,detailEntities);

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Page<PostResponse>> findUserCommissionPosts(UserEntity user, Pageable pageable) {
        Page<CommissionPostEntity> postPage = commissionRepository.findAllByUser(user, pageable);

        // Entity -> DTO 변환
        // 이제 두 인자를 모두 준비했으니 변환 메소드를 호출할 수 있어
        Page<PostResponse> responsePage = postPage.map(commissionPostConverter::toResponse);
        return ResponseEntity.ok(responsePage);
    }
}
