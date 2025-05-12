package com.goodsmoa.goodsmoa_BE.trade.Service;


import com.goodsmoa.goodsmoa_BE.category.Entity.Category;
import com.goodsmoa.goodsmoa_BE.category.Repository.CategoryRepository;
import com.goodsmoa.goodsmoa_BE.elasticsearch.Service.TradePostSearchService;
import com.goodsmoa.goodsmoa_BE.fileUpload.FileUploadService;
import com.goodsmoa.goodsmoa_BE.trade.Converter.TradeImageConverter;
import com.goodsmoa.goodsmoa_BE.trade.Converter.TradePostConverter;
import com.goodsmoa.goodsmoa_BE.trade.DTO.Image.TradeImageRequest;
import com.goodsmoa.goodsmoa_BE.trade.DTO.Image.TradeImageResponse;
import com.goodsmoa.goodsmoa_BE.trade.DTO.Image.TradeImgUpdateRequest;
import com.goodsmoa.goodsmoa_BE.trade.DTO.Post.*;
import com.goodsmoa.goodsmoa_BE.trade.Entity.TradeImageEntity;
import com.goodsmoa.goodsmoa_BE.trade.Entity.TradePostEntity;
import com.goodsmoa.goodsmoa_BE.trade.Repository.TradeImageRepository;
import com.goodsmoa.goodsmoa_BE.trade.Repository.TradePostRepository;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import com.goodsmoa.goodsmoa_BE.user.Repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TradePostService {
    private final TradePostRepository tradePostRepository;
    private final TradePostConverter tradePostConverter;
    private final TradeImageRepository tradeImageRepository;
    private final CategoryRepository categoryRepository;
    private final TradeImageConverter tradeImageConverter;
    private final UserRepository userRepository;
    private final TradePostSearchService tradePostSearchService;
    private final FileUploadService fileUploadService;

    //.
    // 중고거래 글 안에 쓸 사진 등록
//    @Transactional
//    public void addImage(Long postId, TradeImageRequest imageRequests) {
//        TradePostEntity postEntity = tradePostRepository.findById(postId)
//                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
//
//        List<TradeImageEntity> newImages = imageRequests.getImagePath()
//                .stream()
//                .map(imagePath -> TradeImageEntity.builder()
//                        .imagePath(imagePath)
//                        .tradePostEntity(postEntity)
//                        .build())
//                .collect(Collectors.toList());
//
//        postEntity.addImagePath(newImages); // 기존 이미지 유지하고 새로운 이미지 추가
//
//        tradePostRepository.save(postEntity);
//    }

    //   중고거래 글 쓰기
    @Transactional
    public ResponseEntity<TradePostResponse> createTradePost( UserEntity user, TradePostRequest request, TradeImageRequest imageRequest) {
        Category category = categoryRepository.getReferenceById(request.getCategoryId());

//        String thumbnailUrl = null;
//        if(imageRequest.getThumbnailImage() != null) {
//            thumbnailUrl = fileUploadService.uploadSingleImage(imageRequest.getThumbnailImage());
//
//        }
        //썸네일
        //http://localhost:8080/trade/thumbnail/be2fb8e0-5765-4dd6-8e32-715387162fc4_%E1%84%86%E1%85%A6%E1%84%82%E1%85%B2%20%E1%84%80%E1%85%AE%E1%84%89%E1%85%A5%E1%86%BC%E1%84%83%E1%85%A9.png
        String thumbnailUrl = null;
        if (imageRequest.getThumbnailImage() != null) {
            thumbnailUrl = fileUploadService.uploadSingleImage(imageRequest.getThumbnailImage(), "thumbnail");
        }
        //내용 안에 이미지
        List<String> contentUrls = fileUploadService.uploadMultiImages(imageRequest.getContentImages(), "content");
        //http://localhost:8080/trade/content/ + url
        StringBuilder contentWithImages = new StringBuilder(request.getContent());
        for (String url : contentUrls) {
            contentWithImages.append("<br><img src='").append(url).append("'/>");
//            contentWithImages.append(url).append(" ");
        }
        String finalContent = contentWithImages.toString();

        TradePostEntity tradePostEntity = tradePostConverter.toEntity(request, category, user,thumbnailUrl,finalContent);

        tradePostEntity.setUser(user);


        tradePostRepository.save(tradePostEntity);

        List<TradeImageEntity> tradeImageEntities = new ArrayList<>();

        //http://localhost:8080/trade/product/ + url
        //상품 이미지
        List<String> productUrls = fileUploadService.uploadMultiImages(imageRequest.getProductImages(), "product");
        for (String url : productUrls) {
            tradeImageEntities.add(TradeImageEntity.builder()
                    .imagePath(url)
                    .tradePostEntity(tradePostEntity)
                    .build());
        }
//        if (request.getImagePath() != null && !request.getImagePath().isEmpty()) {
//            tradeImageEntities = tradeImageConverter.toEntityList(request, tradePostEntity);
//            tradeImageRepository.saveAll(tradeImageEntities); // 이미지 저장
//        }
//
//        List<String> contentUrl = fileUploadService.uploadMultiImages(imageRequest.getContentImages());
//        for(String url : contentUrl){
//            tradeImageEntities.add(TradeImageEntity.builder()
//                    .imagePath(url)
//                    .tradePostEntity(tradePostEntity)
//                    .build());
//        }
//
//        List<String> productUrls = fileUploadService.uploadMultiImages(imageRequest.getProductImages());
//        for(String url : productUrls){
//            tradeImageEntities.add(TradeImageEntity.builder()
//                    .imagePath(url)
//                    .tradePostEntity(tradePostEntity)
//                    .build());
//        }

        tradeImageRepository.saveAll(tradeImageEntities);

        TradePostResponse response = tradePostConverter.toResponse(tradePostEntity,tradeImageEntities);

        tradePostSearchService.savePost(tradePostEntity); // 엘라스틱 서치 저장

        return ResponseEntity.ok(response);
    }

    //    중고거래 글 업뎃
//    @Transactional
//    public ResponseEntity<TradePostUpdateResponse> updateTradePost(UserEntity user, Long tradePostEntityId , TradePostRequest request) {
//        TradePostEntity tradePostEntity = tradePostRepository.findById(tradePostEntityId).orElseThrow(()-> new EntityNotFoundException("해당 거래글이 존재하지 않습니다."));
//        if(!userRepository.existsById(user.getId())) {
//            return ResponseEntity.notFound().build();
//        }
//        tradePostEntity.updatePost(request);
//        tradePostEntity.updateTradeLocation(request);
//        tradePostEntity.updateTradeOptions(request);
//
//        return ResponseEntity.ok(tradePostConverter.upResponse(tradePostEntity));
//    }
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
            String newThumbnailUrl = fileUploadService.uploadSingleImage(imageRequest.getNewThumbnailImage(), "thumbnail");
            tradePost.updateThumbnailImage(newThumbnailUrl);
        }

        // 2. 본문 이미지 삭제
        if (imageRequest != null && imageRequest.getDeleteContentImageIds() != null) {
            List<String> contentImagePaths = imageRequest.getDeleteContentImageIds();
            for (String path : contentImagePaths) {
                // imagePath가 해당하는 이미지 삭제
                tradeImageRepository.deleteByImagePath(path);
            }
        }

        // 3. 본문 이미지 추가 및 HTML 변환
        StringBuilder contentWithImages = new StringBuilder(request.getContent());
        if (imageRequest != null && imageRequest.getNewContentImages() != null) {
            List<String> contentUrls = fileUploadService.uploadMultiImages(imageRequest.getNewContentImages(), "content");
            for (String url : contentUrls) {
                contentWithImages.append("<br><img src='").append(url).append("'/>");
            }

        }

        // 4. 상품 이미지 삭제
        if (imageRequest != null && imageRequest.getDeleteProductImageIds() != null) {
            tradeImageRepository.deleteAllByIdInBatch(imageRequest.getDeleteProductImageIds());
        }

        // 5. 상품 이미지 추가
        if (imageRequest != null && imageRequest.getNewProductImages() != null) {
            List<String> productUrls = fileUploadService.uploadMultiImages(imageRequest.getNewProductImages(), "product");
            List<TradeImageEntity> newImages = productUrls.stream()
                    .map(url -> TradeImageEntity.builder()
                            .imagePath(url)
                            .tradePostEntity(tradePost)
                            .build())
                    .collect(Collectors.toList());

            List<TradeImageEntity> savedNewImages = tradeImageRepository.saveAll(newImages);

            tradePost.addImageList(newImages);

            List<TradeImgUpdateRequest> responseImages = savedNewImages.stream()
                    .map(img -> TradeImgUpdateRequest.builder()
                            .id(img.getId())
                            .imagePath(img.getImagePath())
                            .build())
                    .toList();
        }

        // 6. 게시글 정보 업데이트
        tradePost.updatePost(request, contentWithImages.toString());
        tradePost.updateTradeLocation(request);
        tradePost.updateTradeOptions(request);

        // 응답에 DTO를 사용하여 변환
        return ResponseEntity.ok(tradePostConverter.upResponse(tradePost));
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

        return ResponseEntity.ok("삭제가 완료되었습니다.");
    }

    //    중고거래 글 조회
    @Transactional
    public ResponseEntity<TradePostDetailResponse> getTradePost(Long id) {
        TradePostEntity tradePostEntity = tradePostRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("해당 게시글이 존재하지 않습니다."));
//        List<TradeImageEntity> images = tradeImageRepository.findAllById(id);

        tradePostEntity.increaseViews();
        tradePostRepository.save(tradePostEntity);
//        log.info(tradePostEntity.getViews().toString());



        return ResponseEntity.ok(tradePostConverter.detailResponse(tradePostEntity));
    }



}

