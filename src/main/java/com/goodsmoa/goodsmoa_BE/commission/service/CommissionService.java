package com.goodsmoa.goodsmoa_BE.commission.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goodsmoa.goodsmoa_BE.category.Entity.Category;
import com.goodsmoa.goodsmoa_BE.category.Repository.CategoryRepository;
import com.goodsmoa.goodsmoa_BE.commission.converter.CommissionDetailConverter;
import com.goodsmoa.goodsmoa_BE.commission.converter.CommissionPostConverter;
import com.goodsmoa.goodsmoa_BE.commission.dto.apply.ReceivedListResponse;
import com.goodsmoa.goodsmoa_BE.commission.dto.apply.SubscriptionListResponse;
import com.goodsmoa.goodsmoa_BE.commission.dto.apply.SubscriptionRequest;
import com.goodsmoa.goodsmoa_BE.commission.dto.apply.SubscriptionResponse;
import com.goodsmoa.goodsmoa_BE.commission.dto.detail.CommissionDetailRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.goodsmoa.goodsmoa_BE.commission.dto.post.*;
import com.goodsmoa.goodsmoa_BE.commission.entity.CommissionDetailEntity;
import com.goodsmoa.goodsmoa_BE.commission.entity.CommissionDetailResponseEntity;
import com.goodsmoa.goodsmoa_BE.commission.entity.CommissionPostEntity;
import com.goodsmoa.goodsmoa_BE.commission.entity.CommissionSubscriptionEntity;
import com.goodsmoa.goodsmoa_BE.commission.repository.CommissionDetailRepository;
import com.goodsmoa.goodsmoa_BE.commission.repository.CommissionDetailResponseRepository;
import com.goodsmoa.goodsmoa_BE.commission.repository.CommissionRepository;
import com.goodsmoa.goodsmoa_BE.commission.repository.CommissionSubscriptionRepository;
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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
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

    private final CommissionSubscriptionRepository commissionSubscriptionRepository;

    private final CommissionDetailResponseRepository commissionDetailResponseRepository;

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

        // 1. 요청에 content가 있을 경우에만 수정
        if (request.getContent() != null) {
            // 1-1. 기존 HTML에서 S3 이미지 URL 추출
            Set<String> oldImageUrls = extractImageUrls(entity.getContent());

            // 1-2. 새로 업로드할 이미지가 있을 경우 S3에 업로드
            List<String> newlyUploadedUrls = new ArrayList<>();
            if (newContentImage != null && !newContentImage.isEmpty()) {
                for (MultipartFile image : newContentImage) {
                    String uploadPath = s3Uploader.upload(image);
                    newlyUploadedUrls.add(uploadPath);
                }
            }

            // 1-3. 요청받은 HTML에서 src placeholder를 S3 URL로 교체
            String newContent = request.getContent();
            Pattern pattern = Pattern.compile("src=[\"'](.*?)[\"']");
            Matcher matcher = pattern.matcher(newContent);
            StringBuffer finalContent = new StringBuffer();
            int imageIndex = 0;

            while (matcher.find()) {
                String src = matcher.group(1);
                // 새로 업로드된 이미지가 있고, 현재 src가 http로 시작하지 않으면 새 URL로 교체
                if (!src.startsWith("http") && imageIndex < newlyUploadedUrls.size()) {
                    String newPath = "src=\"" + newlyUploadedUrls.get(imageIndex++) + "\"";
                    matcher.appendReplacement(finalContent, Matcher.quoteReplacement(newPath));
                }
            }
            matcher.appendTail(finalContent);

            // 1-4. 최종 HTML의 이미지 URL 추출 후 삭제해야 할 이미지 제거
            Set<String> finalImageUrls = extractImageUrls(finalContent.toString());
            oldImageUrls.removeAll(finalImageUrls);
            oldImageUrls.forEach(s3Uploader::delete);

            // 1-5. 완성된 HTML 저장
            entity.setContent(finalContent.toString());
            commissionRepository.save(entity);

        } else{
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

    // 판매자가 올린 커미션 글 리스트로 가져오기
    public ResponseEntity<Page<PostResponse>> findUserCommissionPosts(UserEntity user, Pageable pageable) {
        Page<CommissionPostEntity> postPage = commissionRepository.findAllByUser(user, pageable);

        // Entity -> DTO 변환
        // 이제 두 인자를 모두 준비했으니 변환 메소드를 호출할 수 있어
        Page<PostResponse> responsePage = postPage.map(commissionPostConverter::toResponse);
        return ResponseEntity.ok(responsePage);
    }

    // 커미션 신청
    public ResponseEntity<SubscriptionResponse> subscriptionCommissionPost(UserEntity user, List<SubscriptionRequest> request, List<MultipartFile> contentImages) throws IOException {

        // 1. 커미션 신청 저장
        Long commissionId = request.get(0).getCommissionId();

        CommissionPostEntity postEntity = commissionRepository.findById(commissionId).orElseThrow(() -> new EntityNotFoundException("게시물이 존재하지 않습니다."));

        CommissionSubscriptionEntity subscriptionEntity = commissionPostConverter.saveToSubscriptionEntity(user,postEntity);

        commissionSubscriptionRepository.save(subscriptionEntity);

        // 2. 커미션 상세 신청 저장
        List<String> contentImagePaths = new ArrayList<>();
        List<String> resContent = new ArrayList<>();
        if (contentImages != null && !contentImages.isEmpty()) {
            for (MultipartFile file : contentImages) {
                String path = s3Uploader.upload(file);
                contentImagePaths.add(path);
            }
        }
        int globalImageIndex = 0;
        for (SubscriptionRequest req : request) {
            CommissionDetailEntity detailEntity = commissionDetailRepository.findById(req.getDetailId()).orElse(null);

            CommissionDetailResponseEntity detailResponseEntity = commissionDetailConverter.detailResponseToEntity(user, detailEntity);

            String originalContent = req.getResContent();
            Pattern pattern = Pattern.compile("src=[\"'](.*?)[\"']");
            Matcher matcher = pattern.matcher(originalContent);

            StringBuffer result = new StringBuffer();

            while (matcher.find() && globalImageIndex < contentImagePaths.size()) {
                String newPath = "src='" + contentImagePaths.get(globalImageIndex++) + "'";
                matcher.appendReplacement(result, newPath);
            }
            matcher.appendTail(result);

            detailResponseEntity.setResContent(result.toString());
            resContent.add(result.toString());
            commissionDetailResponseRepository.save(detailResponseEntity);
        }

        List<CommissionDetailEntity> detailEntities = commissionDetailRepository.findByCommissionPostEntity(postEntity);

        SubscriptionResponse response = commissionPostConverter.subscriptionResponse(postEntity,detailEntities,resContent);

        return ResponseEntity.ok(response);
    }

    private Set<String> extractImageUrls(String htmlContent) {
        if (htmlContent == null || htmlContent.isEmpty()) {
            return new HashSet<>();
        }
        Set<String> imageUrls = new HashSet<>();
        Pattern pattern = Pattern.compile("src=[\"'](https?://[^\"']+)[\"']");
        Matcher matcher = pattern.matcher(htmlContent);
        while (matcher.find()) {
            imageUrls.add(matcher.group(1));
        }
        return imageUrls;
    }

    // 내가 신청한 글 가져오기
    public ResponseEntity<Page<SubscriptionListResponse>> findSubscriptionPosts(UserEntity user, Pageable pageable) {

        Page<CommissionSubscriptionEntity> entities = commissionSubscriptionRepository.findByUserId(user, pageable);

        Page<SubscriptionListResponse> responses = entities.map(commissionPostConverter::toSubscriptionListResponse);

        return ResponseEntity.ok(responses);
    }

    // 나한테 요청한 글 가져오기
    public ResponseEntity<Page<ReceivedListResponse>> findReceivedPosts(UserEntity user, Pageable pageable) {

        Page<CommissionSubscriptionEntity> entities = commissionSubscriptionRepository.findByCommissionId_User(user, pageable);

        Page<ReceivedListResponse> responses = entities.map(commissionPostConverter::toReceivedListResponse);

        return ResponseEntity.ok(responses);
    }
}
