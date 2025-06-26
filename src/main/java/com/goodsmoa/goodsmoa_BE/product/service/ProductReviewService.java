package com.goodsmoa.goodsmoa_BE.product.service;

import com.goodsmoa.goodsmoa_BE.config.S3Uploader;
import com.goodsmoa.goodsmoa_BE.fileUpload.FileUploadService;
import com.goodsmoa.goodsmoa_BE.product.converter.ProductReviewConverter;
import com.goodsmoa.goodsmoa_BE.product.dto.post.PostDetailResponse;
import com.goodsmoa.goodsmoa_BE.product.dto.post.PostsResponse;
import com.goodsmoa.goodsmoa_BE.product.dto.review.*;
import com.goodsmoa.goodsmoa_BE.product.entity.ProductPostEntity;
import com.goodsmoa.goodsmoa_BE.product.entity.ProductReportEntity;
import com.goodsmoa.goodsmoa_BE.product.entity.ProductReviewEntity;
import com.goodsmoa.goodsmoa_BE.product.entity.ProductReviewMediaEntity;
import com.goodsmoa.goodsmoa_BE.product.repository.ProductPostRepository;
import com.goodsmoa.goodsmoa_BE.product.repository.ProductReviewMediaRepository;
import com.goodsmoa.goodsmoa_BE.product.repository.ProductReviewRepository;
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
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductReviewService {

    private final ProductPostRepository productPostRepository;

    private final ProductReviewConverter productReviewConverter;
    private final ProductReviewRepository productReviewRepository;

    private final ProductService productService;

    private final ProductReviewMediaRepository productReviewMediaRepository;

    private final S3Uploader s3Uploader;

    // 리뷰 창 보여주기
    public ResponseEntity<ProductReviewDetailResponse> getView(Long reviewId) {
        ProductReviewEntity review = productReviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("리뷰를 찾을 수 없습니다."));

        List<String> mediaUrls = review.getMediaList().stream()
                .map(ProductReviewMediaEntity::getFilePath)
                .toList();

        ProductReviewDetailResponse response = productReviewConverter.toDetailResponse(review, mediaUrls);
        return ResponseEntity.ok(response);
    }

    // 리뷰 생성하기
    public ResponseEntity<PostDetailResponse> createReview(ProductReviewRequest request, UserEntity user, List<MultipartFile> reviewImages) throws IOException {
        // 1. 상품 및 리뷰 생성
        ProductPostEntity postEntity = productPostRepository.findById(request.getPostId())
                .orElseThrow(() -> new EntityNotFoundException("상품글이 존재하지 않습니다."));

        ProductReviewEntity reviewEntity = productReviewConverter.toEntity(request, postEntity, user);

        productReviewRepository.save(reviewEntity);

        // 2. 이미지 업로드
        if (reviewImages != null && !reviewImages.isEmpty()) {
            for (MultipartFile reviewImage : reviewImages) {
                // 개별 이미지 업로드
                String path = s3Uploader.upload(reviewImage);

                // 업로드된 경로를 DB에 저장
                ProductReviewMediaEntity media = ProductReviewMediaEntity.builder()
                        .filePath(path)
                        .review(reviewEntity)
                        .build();

                productReviewMediaRepository.save(media);
            }
        }

        PostDetailResponse response = productService.detailProductPost(request.getPostId()).getBody();
        return ResponseEntity.ok(response);
    }

    // 업데이트
    @Transactional
    public ResponseEntity<PostDetailResponse> updateReview(ProductReviewUpdateRequest request, UserEntity user, List<MultipartFile> newImages) throws IOException {

        // 리뷰 찾기
        ProductReviewEntity review = productReviewRepository.findById(request.getReviewId())
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));

        // 본인인지 확인
        if (!review.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("본인의 리뷰만 수정할 수 있습니다.");
        }

        // 기존 필드 업데이트
        review.setRating(request.getRating());
        review.setContent(request.getContent());
        review.setUpdatedAt(LocalDateTime.now());

        if (request.getDeletedImageIds() != null && !request.getDeletedImageIds().isEmpty()) {
            productReviewMediaRepository.deleteAllByIdIn(request.getDeletedImageIds());
        }

        // 2. 이미지 업로드
        if (newImages != null && !newImages.isEmpty()) {
            for (MultipartFile reviewImage : newImages) {
                // 개별 이미지 업로드
                String path = s3Uploader.upload(reviewImage);

                // 업로드된 경로를 DB에 저장
                ProductReviewMediaEntity media = ProductReviewMediaEntity.builder()
                        .filePath(path)
                        .review(review)
                        .build();

                productReviewMediaRepository.save(media);
            }
        }
        productReviewRepository.save(review);

        PostDetailResponse response = new PostDetailResponse();
        return ResponseEntity.ok(response);
    }

    // 리뷰 삭제 메서드
    @Transactional
    public ResponseEntity<Void> deleteReview(Long id, UserEntity user) {

        ProductReviewEntity entity = productReviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("리뷰가 존재하지 않습니다."));

        if(!entity.getUser().getId().equals(user.getId())) {
            throw new UnsupportedOperationException("작성자만 삭제할 수 있습니다.");
        }

        List<ProductReviewMediaEntity> mediaList = entity.getMediaList();

        // 1. mediaList → ID 리스트로 변환
        List<Long> mediaIds = mediaList.stream()
                .map(ProductReviewMediaEntity::getId)
                .collect(Collectors.toList());

        // 2. ID 기반 삭제
        productReviewMediaRepository.deleteAllByIdIn(mediaIds);

        // 3. 리뷰 삭제
        productReviewRepository.delete(entity);
        return ResponseEntity.ok().build();
    }

    // 상품글에 있는 리스트 조회들
    public ResponseEntity<Page<ProductSummaryResponse>> getReviewList(Long postId, Pageable pageable) {
        ProductPostEntity post = productPostRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("상품 게시글을 찾을 수 없습니다."));

        Page<ProductReviewEntity> reviewPage = productReviewRepository.findByProductPostEntity(post, pageable);
        Page<ProductSummaryResponse> responsePage = reviewPage.map(productReviewConverter::toSummaryResponse);

        return ResponseEntity.ok(responsePage);
    }

    // 내가 만든 리뷰 리스트
    public ResponseEntity<Page<ProductReviewResponse>> getList(UserEntity user, Pageable pageable) {
        Page<ProductReviewEntity> reviewPage = productReviewRepository.findByUser(user,pageable);
        Page<ProductReviewResponse> responsePage = reviewPage.map(productReviewConverter::toMyReviewResponse);
        return ResponseEntity.ok(responsePage);
    }
}
