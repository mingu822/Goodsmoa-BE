package com.goodsmoa.goodsmoa_BE.trade.Service;


import com.goodsmoa.goodsmoa_BE.category.Entity.Category;
import com.goodsmoa.goodsmoa_BE.category.Repository.CategoryRepository;
//import com.goodsmoa.goodsmoa_BE.elasticsearch.Service.TradePostSearchService;
import com.goodsmoa.goodsmoa_BE.fileUpload.FileUploadService;
import com.goodsmoa.goodsmoa_BE.search.service.SearchService;
import com.goodsmoa.goodsmoa_BE.trade.Converter.TradePostConverter;
import com.goodsmoa.goodsmoa_BE.trade.Converter.TradePostDescriptionConverter;
import com.goodsmoa.goodsmoa_BE.trade.DTO.Image.TradeImageRequest;
import com.goodsmoa.goodsmoa_BE.trade.DTO.Post.*;
import com.goodsmoa.goodsmoa_BE.trade.Entity.TradeImageEntity;
import com.goodsmoa.goodsmoa_BE.trade.Entity.TradePostDescription;
import com.goodsmoa.goodsmoa_BE.trade.Entity.TradePostEntity;
import com.goodsmoa.goodsmoa_BE.trade.Repository.TradeImageRepository;
import com.goodsmoa.goodsmoa_BE.trade.Repository.TradePostDescriptionRepository;
import com.goodsmoa.goodsmoa_BE.trade.Repository.TradePostHiddenRepository;
import com.goodsmoa.goodsmoa_BE.trade.Repository.TradePostRepository;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import com.goodsmoa.goodsmoa_BE.user.Repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.goodsmoa.goodsmoa_BE.trade.Entity.TradePostDescription.contentType.IMAGE;
import static com.goodsmoa.goodsmoa_BE.trade.Entity.TradePostDescription.contentType.TEXT;

@Slf4j
@Service
@RequiredArgsConstructor
public class TradePostService {

    private final TradePostViewService tradePostViewService;

    private final TradePostRepository tradePostRepository;
    private final TradePostConverter tradePostConverter;
    private final TradeImageRepository tradeImageRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final SearchService searchService;
    private final FileUploadService fileUploadService;
    private final TradePostHiddenRepository tradePostHiddenRepository;
    private final TradePostDescriptionRepository tradePostDescriptionRepository;
    private final TradePostDescriptionConverter tradePostDescriptionConverter;


    //   중고거래 글 쓰기
    @Transactional
    public ResponseEntity<TradePostResponse> createTradePost(UserEntity user, TradePostRequest request, TradeImageRequest imageRequest) {
        // 1. 카테고리 및 게시글 엔티티 생성
        Category category = categoryRepository.getReferenceById(request.getCategoryId());
        TradePostEntity tradePostEntity = tradePostConverter.toEntity(request, category, user, null);

        tradePostEntity.setUser(user);

        // 2. 게시글 DB에 저장 (ID가 생성됨)
        tradePostRepository.save(tradePostEntity);

        // 3. 저장된 게시글 ID로 파일 업로드
        Long tradePostId = tradePostEntity.getId();

        // 썸네일 이미지 업로드 (trade/thumbnail)

        String thumbnailUrl = null;
        if (imageRequest.getThumbnailImage() != null) {
            thumbnailUrl = fileUploadService.uploadSingleImage(imageRequest.getThumbnailImage(), "/trade/thumbnail", tradePostId);
        }
//
//        // 본문 이미지 업로드 (trade/content)
//        List<String> contentUrls = fileUploadService.uploadMultiImages(imageRequest.getContentImages(), "/trade/content", tradePostId);
//
//        // 본문에 포함된 이미지 URL 삽입
//        StringBuilder contentWithImages = new StringBuilder(request.getContent());
//        for (String url : contentUrls) {
//            contentWithImages.append("<br><img src='/").append(url).append("'/>");
//        }
//        String finalContent = contentWithImages.toString();
//
//        // 게시글 엔티티 업데이트
//        tradePostEntity.setContent(request.getContent());
        List<TradePostDescription> descriptionEntity = new ArrayList<>();
        List<String> contentUrls = fileUploadService.uploadMultiImages(imageRequest.getContentImages(), "/trade/content", tradePostId);
        List<DescriptionDTO> descriptions = request.getDescriptions();
        int imageIndex = 0;
        for (DescriptionDTO description : descriptions) {
            if (description.getType() == IMAGE) {
                if (imageIndex >= contentUrls.size()) {
                    throw new IllegalArgumentException("IMAGE type description에는 이미지 URL이 필요합니다.");
                }
                description.setValue(contentUrls.get(imageIndex++));
            }
            description.sanitize();

            TradePostDescription tradePostDescription = tradePostDescriptionConverter.toEntity(description);
            tradePostDescription.setTradePost(tradePostEntity);
            descriptionEntity.add(tradePostDescription);
        }

//
//            TradePostDescription tradePostDescription = tradePostDescriptionConverter.toEntity(description);
//            tradePostDescription.setTradePost(tradePostEntity);
//            descriptionEntity.add(tradePostDescription);

        tradePostDescriptionRepository.saveAll(descriptionEntity);
        tradePostEntity.setThumbnailUrl(thumbnailUrl);
        tradePostRepository.save(tradePostEntity);

        // 상품 이미지 업로드 (trade/product)
        List<TradeImageEntity> tradeImageEntities = new ArrayList<>();
        List<String> productUrls = fileUploadService.uploadMultiImages(imageRequest.getProductImages(), "/trade/product", tradePostId);
        for (String url : productUrls) {
            tradeImageEntities.add(TradeImageEntity.builder()
                    .imagePath(url)
                    .tradePostEntity(tradePostEntity)
                    .build());
        }

        // 이미지 정보 저장
        tradeImageRepository.saveAll(tradeImageEntities);

        // 응답 준비
        TradePostResponse response = tradePostConverter.toResponse(tradePostEntity, tradeImageEntities, descriptionEntity);
        // 엘라스틱 서치 저장

        tradePostEntity.setContentDescriptions(descriptionEntity);

        searchService.saveOrUpdateDocument(tradePostEntity);
        return ResponseEntity.ok(response);
    }


    //    중고거래 글 업뎃
    @Transactional
    public ResponseEntity<TradePostUpdateResponse> updateTradePost(UserEntity user, Long tradePostId,
                                                                   TradePostRequest request, TradeImageUpdateRequest imageRequest) {
        TradePostEntity tradePost = tradePostRepository.findById(tradePostId)
                .orElseThrow(() -> new EntityNotFoundException("거래 글이 존재하지 않습니다."));

//        if (!user.getId().equals(tradePost.getUser().getId())) {
//            throw new UnsupportedOperationException("글 작성자만 수정할 수 있습니다.");
//        }
        if(!userRepository.existsById(user.getId())) {
            return ResponseEntity.notFound().build();
        }

        // 1. 썸네일 이미지 교체
        if (imageRequest != null && imageRequest.getNewThumbnailImage() != null) {

            String newThumbnailUrl = fileUploadService.uploadSingleImage(imageRequest.getNewThumbnailImage(), "/trade/thumbnail",tradePostId);
            tradePost.updateThumbnailImage(newThumbnailUrl);
        }

        // 2 내용 , 내용 이미지 교체
        // 기존 Description 제거 후 새로 등록
        // ✅ 기존 Description 삭제 후 즉시 반영
        // 1. 기존 Description 삭제
        tradePostDescriptionRepository.deleteByTradePost(tradePost);
        tradePostDescriptionRepository.flush(); // 영속성 컨텍스트에서도 제거!

// 2. 새 Description 생성 (중복된 객체를 절대 재사용하면 안 됨!)
        List<String> uploadedContentImageUrls = Optional.ofNullable(imageRequest.getNewContentImages())
                .filter(list -> !list.isEmpty())
                .map(images -> fileUploadService.uploadMultiImages(images, "/trade/content", tradePostId))
                .orElse(Collections.emptyList());

        int imageIndex = 0;
        List<TradePostDescription> newDescriptions = new ArrayList<>();

        List<DescriptionDTO> sortedDescriptions = request.getDescriptions().stream()
                .sorted(Comparator.comparingInt(DescriptionDTO::getSequence))
                .toList();

        for (DescriptionDTO dto : sortedDescriptions) {
            dto.sanitize();
            String value;
            if (dto.getType() == TEXT) {
                value = dto.getValue();
            } else if (dto.getType() == IMAGE) {
                if (imageIndex >= uploadedContentImageUrls.size()) {
                    throw new IllegalArgumentException("IMAGE 타입 Description 개수보다 업로드된 이미지 수가 부족합니다. 업로드됨: " + uploadedContentImageUrls.size());
                }
                value = uploadedContentImageUrls.get(imageIndex++);
            } else {
                throw new IllegalArgumentException("알 수 없는 Description 타입입니다: " + dto.getType());
            }

            // ✨ 무조건 새로 생성된 객체만 저장합니다.
            newDescriptions.add(TradePostDescription.builder()
                    .tradePost(tradePost)
                    .contentType(dto.getType())
                    .value(value)
                    .sequence(dto.getSequence())
                    .build());
//            TradePostDescription description = tradePostDescriptionConverter.toEntity(dto);
//            description.setTradePost(tradePost); // 연관관계 설정
//            newDescriptions.add(description);

        }


// 3. 새로 만든 Description 저장
        tradePostDescriptionRepository.saveAll(newDescriptions);


        // 2. 본문 이미지 삭제
//        if (imageRequest != null && imageRequest.getDeleteContentImageIds() != null) {
//            List<String> contentImagePaths = imageRequest.getDeleteContentImageIds();
//            for (String path : imageRequest.getDeleteContentImageIds()) {
//                // imagePath가 해당하는 이미지 삭제
//                tradeImageRepository.deleteByImagePath(path);
//            }
//        }
//
//        // 3. 본문 이미지 추가 및 HTML 변환
//        StringBuilder contentWithImages = new StringBuilder(request.getContent());
//        List<String> newContentUrl = new ArrayList<>();
//        if (imageRequest != null && imageRequest.getNewContentImages() != null) {
//            List<String> contentUrls = fileUploadService.uploadMultiImages(imageRequest.getNewContentImages(), "trade/content",tradePostId);
//            for (String url : contentUrls) {
//                contentWithImages.append("<br><img src='/").append(url).append("'/>");
//            }
//            newContentUrl = fileUploadService.uploadMultiImages(imageRequest.getNewContentImages(), "/trade/content", tradePostId);
//
//        }

        // 4. 상품 이미지 삭제
        if (imageRequest != null && imageRequest.getDeleteProductImageIds() != null) {
            tradeImageRepository.deleteAllByIdInBatch(imageRequest.getDeleteProductImageIds());
        }

        // 5. 상품 이미지 추가
        if (imageRequest != null && imageRequest.getNewProductImages() != null) {
            List<String> productUrls = fileUploadService.uploadMultiImages(imageRequest.getNewProductImages(), "/trade/product",tradePostId);
            List<TradeImageEntity> newImages = productUrls.stream()
                    .map(url -> TradeImageEntity.builder()
                            .imagePath(url)
                            .tradePostEntity(tradePost)
                            .build())
                    .collect(Collectors.toList());

//            List<TradeImageEntity> savedNewImages = tradeImageRepository.saveAll(newImages);
            tradeImageRepository.saveAll(newImages);
            tradePost.addImageList(newImages);

//            List<TradeImgUpdateRequest> responseImages = savedNewImages.stream()
//                    .map(img -> TradeImgUpdateRequest.builder()
//                            .id(img.getId())
//                            .imagePath(img.getImagePath())
//                            .build())
//                    .toList();
        }

        // 6. 게시글 정보 업데이트
        tradePost.updatePost(request);
        tradePost.updateTradeLocation(request);
        tradePost.updateTradeOptions(request);

        TradePostUpdateResponse response = tradePostConverter.upResponse(tradePost);
//        response.setContentImageUrls(newContentUrl);
        tradePostRepository.save(tradePost);
        searchService.saveOrUpdateDocument(tradePost);
        // 응답에 DTO를 사용하여 변환
        return ResponseEntity.ok(response);
    }

    // 끌어올림 시간
    @Transactional
    public ResponseEntity<TradePostPulledResponse> pullPost(Long id){
        TradePostEntity tradePostEntity = tradePostRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("해당글이 존재하지 않습니다."));
        tradePostEntity.pullAt(LocalDateTime.now());
        tradePostRepository.save(tradePostEntity);
        return ResponseEntity.ok(tradePostConverter.pulledResponse(tradePostEntity));

    }

    //    중고거래 글 삭제
    @Transactional
    public ResponseEntity<String> deleteTradePost(@AuthenticationPrincipal UserEntity user, Long id) {
        TradePostEntity tradePostEntity = tradePostRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("해당글이 존재하지 않습니다."));
        if(!tradePostEntity.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }
        tradePostRepository.deleteById(id);
        searchService.deletePostDocument("TRADE_"+id);
        return ResponseEntity.ok("삭제가 완료되었습니다.");
    }

    //    중고거래 글 조회
    @Transactional
    public ResponseEntity<TradePostDetailResponse> getTradePost(Long id) {
        TradePostEntity tradePostEntity = tradePostRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("해당 게시글이 존재하지 않습니다."));

        tradePostViewService.increaseViewCount(id);

        return ResponseEntity.ok(tradePostConverter.detailResponse(tradePostEntity));
    }
    // 로그인한 유저 기준으로 숨김 처리된 게시물 제외하고 조회
    public ResponseEntity<Page<TradePostLookResponse>> getTradePostList(UserEntity user, Pageable pageable) {
        Page<TradePostEntity> tradePostEntityPage;

        // ✅ 1. 로그인 여부 확인
        if (user == null) {
            // 로그인하지 않은 사용자: 모든 게시물을 보여줌
            tradePostEntityPage = tradePostRepository.findAll(pageable);
        } else {
            // 로그인한 사용자: 기존 로직을 그대로 실행
            List<Long> hiddenPostIds = tradePostHiddenRepository.findAllByUser(user).stream()
                    .map(h -> h.getTradePost().getId())
                    .toList();

            if (hiddenPostIds.isEmpty()) {
                // 숨긴 게시물이 없으면 전체 조회
                tradePostEntityPage = tradePostRepository.findAll(pageable);
            } else {
                // 숨긴 게시물을 제외하고 조회
                tradePostEntityPage = tradePostRepository.findByIdNotIn(hiddenPostIds, pageable);
            }
        }

        // DTO 변환 및 반환 로직은 공통이므로 그대로 둡니다.
        Page<TradePostLookResponse> responsePage = tradePostEntityPage.map(tradePostConverter::lookResponse);
        return ResponseEntity.ok(responsePage);
    }

//    @Transactional(readOnly = true)
//    public Page<TradePostLookResponse> getTradePostListByUser(UserEntity user, TradeSearchRequest request) {
//        // 1. 숨김 처리한 게시글 ID 목록 조회
//        List<Long> hiddenPostIds = tradePostHiddenRepository.findAllByUser(user).stream()
//                .map(h -> h.getTradePost().getId())
//                .toList();
//
//        // 2. 페이징 정보 생성 (최신순 정렬)
//        PageRequest pageRequest = PageRequest.of(
//                request.getPage(),
//                request.getPageSize(),
//                Sort.by(Sort.Direction.DESC, "createdAt")
//        );
//
//        Page<TradePostEntity> pageResult;
//
//        // 3. 조건에 따라 분기 처리하여 데이터 조회
//        boolean hasCategory = request.getCategoryId() != null && request.getCategoryId() != 0;
//        boolean hasHiddenPosts = !hiddenPostIds.isEmpty();
//
//        if (hasCategory) {
//            Category category = categoryRepository.findById(request.getCategoryId())
//                    .orElseThrow(() -> new EntityNotFoundException("카테고리를 찾을 수 없습니다."));
//            if (hasHiddenPosts) {
//                // 카테고리 필터 O, 숨김 글 O
//                pageResult = tradePostRepository.findByUserAndCategoryAndIdNotIn(user, category, hiddenPostIds, pageRequest);
//            } else {
//                // 카테고리 필터 O, 숨김 글 X
//                pageResult = tradePostRepository.findByUserAndCategory(user, category, pageRequest);
//            }
//        } else {
//            if (hasHiddenPosts) {
//                // 카테고리 필터 X, 숨김 글 O
//                pageResult = tradePostRepository.findByUserAndIdNotIn(user, hiddenPostIds, pageRequest);
//            } else {
//                // 카테고리 필터 X, 숨김 글 X
//                pageResult = tradePostRepository.findByUser(user, pageRequest);
//            }
//        }
//
//        // 4. 조회 결과를 DTO로 변환하여 반환
//        return pageResult.map(tradePostConverter::lookResponse); // toLookResponse는 너의 컨버터 메소드 이름에 맞게 수정
//    }

    @Transactional
    public TradeStatusUpdateResponse updateTradeStatus(Long tradePostId, String newStatusString) {
        // 1. ID로 게시글을 찾고, 없으면 예외 발생
        TradePostEntity post = tradePostRepository.findById(tradePostId)
                .orElseThrow(() -> new IllegalArgumentException("ID에 해당하는 게시글이 없습니다: " + tradePostId));

        // 2. 문자열로 받은 상태 값을 Enum 타입으로 변환
        TradePostEntity.TradeStatus newStatus;
        try {
            // "완료" 라는 문자열을 TradeStatus.완료 Enum으로 변환
            newStatus = TradePostEntity.TradeStatus.valueOf(newStatusString);
        } catch (IllegalArgumentException e) {
            // 만약 "판매중", "완료"가 아닌 다른 이상한 문자열이 들어오면 예외 발생
            throw new IllegalArgumentException("유효하지 않은 거래 상태 값입니다: " + newStatusString);
        }

        // 3. 게시글의 상태를 변경
        post.setTradeStatus(newStatus);

        // 4. @Transactional 어노테이션 덕분에 메서드가 끝나면 변경된 내용이 자동으로 DB에 저장(save)돼.
        // tradePostRepository.save(post); // 명시적으로 호출해도 괜찮아.

        // 5. 변경된 상태를 포함한 응답 DTO를 생성해서 반환
        return TradeStatusUpdateResponse.builder()
                .tradeStatus(post.getTradeStatus().name()) // "완료"
                .build();
    }



}

