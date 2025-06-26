package com.goodsmoa.goodsmoa_BE.trade.Service;


import com.goodsmoa.goodsmoa_BE.cart.entity.OrderEntity;
import com.goodsmoa.goodsmoa_BE.cart.repository.OrderRepository;
import com.goodsmoa.goodsmoa_BE.category.Entity.Category;
import com.goodsmoa.goodsmoa_BE.category.Repository.CategoryRepository;
//import com.goodsmoa.goodsmoa_BE.elasticsearch.Service.TradePostSearchService;
import com.goodsmoa.goodsmoa_BE.config.S3Uploader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashSet;
import java.util.Set;

import com.goodsmoa.goodsmoa_BE.enums.Board;
import com.goodsmoa.goodsmoa_BE.fileUpload.FileUploadService;
import com.goodsmoa.goodsmoa_BE.search.service.SearchService;
import com.goodsmoa.goodsmoa_BE.trade.Converter.TradePostConverter;
import com.goodsmoa.goodsmoa_BE.trade.DTO.Image.TradeImageRequest;
import com.goodsmoa.goodsmoa_BE.trade.DTO.Post.*;
import com.goodsmoa.goodsmoa_BE.trade.Entity.TradeImageEntity;
import com.goodsmoa.goodsmoa_BE.trade.Entity.TradePostEntity;
import com.goodsmoa.goodsmoa_BE.trade.Repository.TradeImageRepository;
import com.goodsmoa.goodsmoa_BE.trade.Repository.TradePostHiddenRepository;
import com.goodsmoa.goodsmoa_BE.trade.Repository.TradePostRepository;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import com.goodsmoa.goodsmoa_BE.user.Repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;



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
    private final S3Uploader s3Uploader;
    private final OrderRepository orderRepository;
    // --- Helper Methods for S3 Upload ---

    // 단일 이미지 업로드 헬퍼 메서드
    private String uploadImage(MultipartFile file) {
        try {
            return s3Uploader.upload(file);
        } catch (IOException e) {
            // e.printStackTrace(); // 로깅 프레임워크 사용 권장 (예: log.error(...))
            throw new RuntimeException("S3 이미지 업로드에 실패했습니다.", e);
        }
    }

    private List<String> uploadImages(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) return new ArrayList<>();
        return files.stream()
                .map(this::uploadImage)
                .collect(Collectors.toList());
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

    @Transactional
    public ResponseEntity<TradePostResponse> createTradePost(UserEntity user, TradePostRequest request, TradeImageRequest imageRequest) {
        if (user == null) {
            throw new IllegalArgumentException("로그인된 사용자만 글을 작성할 수 있습니다.");
        }
        // 1. DTO로부터 기본 엔티티 생성
        // request.getContent()는 아직 이미지 경로가 임시값(placeholder)으로 채워진 상태
        Category category = categoryRepository.getReferenceById(request.getCategoryId());
        TradePostEntity tradePostEntity = tradePostConverter.toEntity(request, category, user);
        tradePostEntity.setUser(user);

        // 2. 썸네일 및 하단 상품 이미지 처리 (본문 내용과 무관한 이미지들)
        // 2-1. 썸네일 이미지 업로드
        if (imageRequest.getThumbnailImage() != null && !imageRequest.getThumbnailImage().isEmpty()) {
            String thumbnailUrl = uploadImage(imageRequest.getThumbnailImage());
            tradePostEntity.setThumbnailImage(thumbnailUrl);
        }

        // 2-2. 하단 상품 이미지 업로드
        List<String> productUrls = uploadImages(imageRequest.getProductImages());
        if (!productUrls.isEmpty()) {
            List<TradeImageEntity> tradeImageEntities = new ArrayList<>();
            for (String url : productUrls) {
                tradeImageEntities.add(TradeImageEntity.builder()
                        .imageUrl(url)
                        .tradePostEntity(tradePostEntity)
                        .build());
            }
            // 이 시점에서는 아직 tradePostEntity에 연결만 하고, 저장은 마지막에 한번에 처리
            tradePostEntity.setImage(tradeImageEntities);
        }

        // ✨ 3. 본문(content) HTML 후처리 (핵심 로직) ✨
        // 3-1. 본문 이미지(contentImages)가 있는 경우에만 처리
        List<MultipartFile> contentImages = imageRequest.getContentImages();
        if (contentImages != null && !contentImages.isEmpty()) {
            // 3-2. 본문 이미지들을 모두 S3에 업로드하고 URL 리스트를 받음
            List<String> contentImageUrls = uploadImages(contentImages);

            // 3-3. 기존 content HTML에서 임시 이미지 경로를 실제 S3 URL로 교체
            String originalContent = request.getContent();

            // 정규표현식으로 src 속성을 찾음
            Pattern pattern = Pattern.compile("src=[\"'](.*?)[\"']");
            Matcher matcher = pattern.matcher(originalContent);

            StringBuffer result = new StringBuffer();
            int i = 0;
            // HTML 안의 src 속성을 순서대로 찾아서, 업로드된 S3 URL로 교체
            while (matcher.find() && i < contentImageUrls.size()) {
                String newPath = "src=\"" + contentImageUrls.get(i++) + "\"";
                matcher.appendReplacement(result, Matcher.quoteReplacement(newPath));
            }
            matcher.appendTail(result);

            // 3-4. 최종적으로 완성된 HTML을 엔티티에 설정
            tradePostEntity.setContent(result.toString());
        }

        // 4. 모든 정보가 채워진 최종 엔티티를 DB에 저장
        TradePostEntity savedEntity = tradePostRepository.save(tradePostEntity);

        // 5. 검색 엔진(Elasticsearch) 데이터 동기화
        searchService.saveOrUpdateDocument(savedEntity);

        // 6. 최종 응답 생성
        TradePostResponse response = tradePostConverter.toResponse(savedEntity);
        return ResponseEntity.ok(response);
    }



    // TradePostService.java

    @Transactional
    public ResponseEntity<TradePostUpdateResponse> updateTradePost(UserEntity user, Long tradePostId,
                                                                   TradePostRequest request, TradeImageUpdateRequest imageRequest) {
        // 1. 게시글 조회 및 권한 확인
        TradePostEntity tradePost = tradePostRepository.findById(tradePostId)
                .orElseThrow(() -> new EntityNotFoundException("거래 글이 존재하지 않습니다."));

        if (!tradePost.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // 2. 썸네일 이미지 교체 (요청이 있을 경우에만)
        if (imageRequest.getNewThumbnailImage() != null && !imageRequest.getNewThumbnailImage().isEmpty()) {
            s3Uploader.delete(tradePost.getThumbnailImage());
            String newThumbnailUrl = uploadImage(imageRequest.getNewThumbnailImage());
            tradePost.updateThumbnailImage(newThumbnailUrl);
        }

        // 3. 본문(content) HTML 수정 처리 (요청에 content가 있을 경우에만)
        if (request.getContent() != null) {
            // 3-1. 기존 HTML에서 S3 이미지 URL들을 모두 추출해서 보관
            Set<String> oldImageUrls = extractImageUrls(tradePost.getContent());

            // 3-2. 새로 업로드할 본문 이미지들을 S3에 업로드
            List<String> newlyUploadedUrls = uploadImages(imageRequest.getNewContentImages());

            // 3-3. 새로 받은 HTML에서 임시 경로(placeholder)를 실제 S3 URL로 교체
            String newContent = request.getContent();
            Pattern pattern = Pattern.compile("src=[\"'](.*?)[\"']");
            Matcher matcher = pattern.matcher(newContent);
            StringBuffer finalContent = new StringBuffer();
            int imageIndex = 0;
            while (matcher.find()) {
                String src = matcher.group(1);
                // src가 http로 시작하지 않으면 (즉, 임시 경로이면) 새로 업로드된 URL로 교체
                if (!src.startsWith("http") && imageIndex < newlyUploadedUrls.size()) {
                    String newPath = "src=\"" + newlyUploadedUrls.get(imageIndex++) + "\"";
                    matcher.appendReplacement(finalContent, Matcher.quoteReplacement(newPath));
                }
            }
            matcher.appendTail(finalContent);

            // 3-4. 최종적으로 완성된 HTML에서 S3 이미지 URL들을 모두 추출
            Set<String> finalImageUrls = extractImageUrls(finalContent.toString());

            // 3-5. 기존 이미지 목록과 최종 이미지 목록을 비교하여, 삭제된 이미지를 S3에서 제거
            oldImageUrls.removeAll(finalImageUrls);
            oldImageUrls.forEach(s3Uploader::delete);

            // 3-6. 최종 완성된 HTML을 엔티티에 설정
            tradePost.setContent(finalContent.toString());
        }

        // 4. 하단 상품 이미지 삭제 및 추가 (요청이 있을 경우에만)
        if (imageRequest != null) {
            // 4-1. 삭제할 상품 이미지 처리 (DB & S3)
            if (imageRequest.getDeleteProductImageIds() != null && !imageRequest.getDeleteProductImageIds().isEmpty()) {
                List<TradeImageEntity> imagesToDelete = tradeImageRepository.findAllById(imageRequest.getDeleteProductImageIds());
                imagesToDelete.forEach(img -> s3Uploader.delete(img.getImageUrl()));
                tradeImageRepository.deleteAllInBatch(imagesToDelete);
            }
            // 4-2. 새로 추가할 상품 이미지 처리 (S3 & DB)
            List<String> newProductUrls = uploadImages(imageRequest.getNewProductImages());
            if (!newProductUrls.isEmpty()) {
                List<TradeImageEntity> newImages = newProductUrls.stream()
                        .map(url -> TradeImageEntity.builder()
                                .imageUrl(url)
                                .tradePostEntity(tradePost)
                                .build())
                        .collect(Collectors.toList());
                tradeImageRepository.saveAll(newImages);
                tradePost.addImageList(newImages);
            }
        }

        // 5. 게시글 텍스트 정보 업데이트
        tradePost.updatePost(request);
        tradePost.updateTradeLocation(request);
        tradePost.updateTradeOptions(request);

        // 6. 검색 엔진 및 최종 저장
        searchService.saveOrUpdateDocument(tradePost);
        tradePostRepository.save(tradePost);

        // 7. 응답 생성
        TradePostUpdateResponse response = tradePostConverter.upResponse(tradePost);
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
    public ResponseEntity<String> deleteTradePost(UserEntity user, Long id) {
        // 1. 삭제할 게시글을 DB에서 조회
        TradePostEntity tradePost = tradePostRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 게시글이 존재하지 않습니다."));

        // 2. 권한 확인 (게시글 작성자와 삭제 요청자가 같은지)
        if (!tradePost.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("삭제 권한이 없습니다.");
        }
        List<OrderEntity> relatedOrders = orderRepository.findByTradePost_Id(id);

        for (OrderEntity order : relatedOrders) {
            order.setTradePost(null); // 주문 기록에서 게시글 정보만 제거
        }

        // --- 3. S3에서 모든 관련 이미지 삭제 (DB보다 먼저!) ---

        // 3-1. 본문(content)에 포함된 이미지들 삭제
        // 이전에 만든 extractImageUrls 헬퍼 메서드를 재활용
        Set<String> contentImageUrls = extractImageUrls(tradePost.getContent());
        contentImageUrls.forEach(s3Uploader::delete);

        // 3-2. 하단 상품 이미지들 삭제

        tradePost.getImage().forEach(imageEntity -> s3Uploader.delete(imageEntity.getImageUrl()));

        // 3-3. 썸네일 이미지 삭제
        s3Uploader.delete(tradePost.getThumbnailImage());

        tradePost.getImage().clear();
        // --- 4. 데이터베이스에서 게시글 삭제 ---
        // Cascade 옵션에 의해 연관된 상품 이미지(TradeImageEntity)들도 함께 삭제됨
        tradePostRepository.delete(tradePost);

        // 5. 검색 엔진(Elasticsearch) 데이터 삭제 (필요하다면)
        searchService.deletePostDocument(Board.TRADE.name() + "_" + id);


        return ResponseEntity.ok("게시글이 성공적으로 삭제되었습니다.");
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

