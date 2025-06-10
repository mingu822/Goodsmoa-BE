package com.goodsmoa.goodsmoa_BE.product.service;

import com.goodsmoa.goodsmoa_BE.fileUpload.FileUploadService;
import com.goodsmoa.goodsmoa_BE.product.converter.ProductReviewConverter;
import com.goodsmoa.goodsmoa_BE.product.dto.post.PostDetailResponse;
import com.goodsmoa.goodsmoa_BE.product.dto.review.ProductReviewRequest;
import com.goodsmoa.goodsmoa_BE.product.dto.review.ProductSummaryResponse;
import com.goodsmoa.goodsmoa_BE.product.entity.ProductPostEntity;
import com.goodsmoa.goodsmoa_BE.product.entity.ProductReportEntity;
import com.goodsmoa.goodsmoa_BE.product.entity.ProductReviewEntity;
import com.goodsmoa.goodsmoa_BE.product.entity.ProductReviewMediaEntity;
import com.goodsmoa.goodsmoa_BE.product.repository.ProductPostRepository;
import com.goodsmoa.goodsmoa_BE.product.repository.ProductReviewMediaRepository;
import com.goodsmoa.goodsmoa_BE.product.repository.ProductReviewRepository;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductReviewService {

    private final ProductPostRepository productPostRepository;

    private final ProductReviewConverter productReviewConverter;
    private final ProductReviewRepository productReviewRepository;

    private final ProductService productService;

    private final FileUploadService fileUploadService;

    private final ProductReviewMediaRepository productReviewMediaRepository;

    // 리뷰 창 보여주기
    public ResponseEntity<ProductSummaryResponse> getView(Long productId) {
        ProductPostEntity product = productPostRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("상품글이 존재하지 않습니다."));

        ProductSummaryResponse response = productReviewConverter.toResponse(product);

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<PostDetailResponse> createReview(ProductReviewRequest request, UserEntity user, List<MultipartFile> reviewImages) {
        // 1. 상품 및 리뷰 생성
        ProductPostEntity postEntity = productPostRepository.findById(request.getPostId())
                .orElseThrow(() -> new EntityNotFoundException("상품글이 존재하지 않습니다."));
        ProductReviewEntity reviewEntity = productReviewConverter.toEntity(request, postEntity, user);
        productReviewRepository.save(reviewEntity);

        // 2. 이미지/비디오 업로드
        List<String> mediaPaths = fileUploadService.uploadMultiImages(reviewImages, "productPost/review", reviewEntity.getId());

        for (String path : mediaPaths) {
            String extension = path.substring(path.lastIndexOf(".") + 1);
            String type = switch (extension) {
                case "jpg", "jpeg", "png", "gif" -> "image";
                case "mp4", "mov", "avi" -> "video";
                default -> "unknown";
            };

            ProductReviewMediaEntity media = ProductReviewMediaEntity
                    .builder()
                    .filePath(path)
                    .review(reviewEntity)
                    .fileType(type)
                    .build();
            productReviewMediaRepository.save(media);
        }

        PostDetailResponse response = productService.detailProductPost(request.getPostId()).getBody();
        return ResponseEntity.ok(response);
    }
    // 업데이트
    public ResponseEntity<PostDetailResponse> updateReview(ProductReviewRequest request, UserEntity user) {

        PostDetailResponse response = null;

        return ResponseEntity.ok(response);
    }

    // 리뷰 삭제 메서드
    public ResponseEntity<Void> deleteReview(Long id, UserEntity user) {

        ProductReviewEntity entity = productReviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("리뷰가 존재하지 않습니다."));

        if(!entity.getUser().equals(user)) {
            throw new UnsupportedOperationException("작성자만 삭제할 수 있습니다.");
        }
        productReviewRepository.delete(entity);
        return ResponseEntity.ok().build();
    }

}
