package com.goodsmoa.goodsmoa_BE.product.service;

import com.goodsmoa.goodsmoa_BE.category.Entity.Category;
import com.goodsmoa.goodsmoa_BE.category.Repository.CategoryRepository;
import com.goodsmoa.goodsmoa_BE.fileUpload.FileUploadService;
import com.goodsmoa.goodsmoa_BE.product.converter.ProductConverter;
import com.goodsmoa.goodsmoa_BE.product.converter.ProductDeliveryConverter;
import com.goodsmoa.goodsmoa_BE.product.converter.ProductImageUpdateConverter;
import com.goodsmoa.goodsmoa_BE.product.converter.ProductPostConverter;
import com.goodsmoa.goodsmoa_BE.product.dto.Delivery.ProductDeliveryRequest;
import com.goodsmoa.goodsmoa_BE.product.dto.Delivery.ProductDeliveryResponse;
import com.goodsmoa.goodsmoa_BE.product.dto.Image.ProductImageUpdateRequest;
import com.goodsmoa.goodsmoa_BE.product.dto.Post.*;
import com.goodsmoa.goodsmoa_BE.product.dto.ProductRequest;
import com.goodsmoa.goodsmoa_BE.product.dto.ProductResponse;
import com.goodsmoa.goodsmoa_BE.product.entity.ProductDeliveryEntity;
import com.goodsmoa.goodsmoa_BE.product.entity.ProductEntity;
import com.goodsmoa.goodsmoa_BE.product.entity.ProductPostEntity;
import com.goodsmoa.goodsmoa_BE.product.repository.ProductDeliveryRepository;
import com.goodsmoa.goodsmoa_BE.product.repository.ProductPostRepository;
import com.goodsmoa.goodsmoa_BE.product.repository.ProductRepository;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductPostViewService productPostViewService;

    private final ProductConverter productConverter;
    private final ProductRepository productRepository;

    private final ProductPostConverter productPostConverter;
    private final ProductPostRepository productPostRepository;

    private final ProductDeliveryConverter productDeliveryConverter;
    private final ProductDeliveryRepository productDeliveryRepository;

    private final CategoryRepository categoryRepository;

    private final FileUploadService fileUploadService;

    private final ProductImageUpdateConverter productImageUpdateConverter;

    // 상품글 생성
    @Transactional
    public ResponseEntity<PostDetailResponse> createPost(
            UserEntity user,
            PostRequest request,
            MultipartFile thumbnailImage,
            List<MultipartFile> productImages,
            List<MultipartFile> contentImages
    ) {
        Category category = categoryRepository.getReferenceById(request.getCategoryId());

        // 게시글 생성 및 저장 (썸네일은 아직 없음)
        ProductPostEntity entity = productPostConverter.createToEntity(request, user, category);
        ProductPostEntity saveEntity = productPostRepository.save(entity);

        Long postId = saveEntity.getId(); // 파일명에 사용할 ID

        // ✅ 썸네일 이미지 저장
        String thumbnailPath = fileUploadService.uploadSingleImage(thumbnailImage, "productPost/thumbnail", postId);
        saveEntity.setThumbnailImage(thumbnailPath); // 썸네일 경로 반영
        productPostRepository.save(saveEntity);

        // ✅ 상품 목록 + 이미지 저장
        List<ProductEntity> products = new ArrayList<>();
        if (request.getProducts() != null && !request.getProducts().isEmpty()) {

            // 이미지가 전달되었는지 확인하고 저장
            List<String> productImagePaths = new ArrayList<>();
            if (productImages != null && !productImages.isEmpty()) {
                productImagePaths = fileUploadService.uploadMultiImages(productImages, "productPost/product", postId);
            }

            // 상품 생성 및 이미지 경로 매핑
            int index = 0;
            for (ProductRequest productRequest : request.getProducts()) {
                ProductEntity productEntity = productConverter.toEntity(saveEntity, productRequest);
                // 이미지 경로가 있는 경우 매핑
                if (index < productImagePaths.size()) {
                    productEntity.setImage(productImagePaths.get(index)); // image 필드 존재 필요
                }
                products.add(productEntity);
                index++;
            }
            productRepository.saveAll(products);         // 상품 저장
            saveEntity.setState(true);                   // 상품이 있을 경우 상태 true
            productPostRepository.save(saveEntity);      // 상태 반영 저장
        }

        // ✅ 상세 이미지 저장 및 본문에 삽입
        if (contentImages != null && !contentImages.isEmpty()) {
            List<String> contentImagePaths = fileUploadService.uploadMultiImages(contentImages, "productPost/content", postId);

            // 기존 content + 이미지 <img> 태그들 추가
            StringBuilder contentWithImages = new StringBuilder(saveEntity.getContent()); // 또는 request.getContent()
            for (String imagePath : contentImagePaths) {
                contentWithImages.append("<br><img src='").append(imagePath).append("'/>");
            }

            // 최종 본문 세팅
            saveEntity.setContent(contentWithImages.toString());
            productPostRepository.save(saveEntity);
        }

        // ✅ 배달 옵션 저장
        List<ProductDeliveryEntity> delivers = new ArrayList<>();
        if (request.getDelivers() != null && !request.getDelivers().isEmpty()) {
            delivers = request.getDelivers().stream()
                    .map(productDeliveryRequest -> productDeliveryConverter.toEntity(productDeliveryRequest, saveEntity))
                    .toList();
            productDeliveryRepository.saveAll(delivers);
        }

        // ✅ 응답 변환
        PostDetailResponse response = productPostConverter.detailToResponse(products, delivers, saveEntity);
        return ResponseEntity.ok(response);
    }


    // 상품글 업데이트 -> 상품글 업데이트 시 상품과 배달 방식이 수정이 있으면 동시에 수정
    @Transactional
    public ResponseEntity<PostDetailResponse> updateProductPost(UserEntity user, Long postId, PostRequest request,
                                                                MultipartFile newThumbnailImage, List<MultipartFile> newContentImages, List<MultipartFile> newProductImages,
                                                                List<String> deleteContentImagePaths, List<Long> deleteProductImageIds) {

        ProductImageUpdateRequest imageRequest = productImageUpdateConverter.toUpdate(newThumbnailImage, newContentImages, newProductImages, deleteContentImagePaths, deleteProductImageIds);


        ProductPostEntity post = productPostRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다."));

        if (!user.getId().equals(post.getUser().getId())) {
            throw new UnsupportedOperationException("작성자만 수정할 수 있습니다.");
        }

        // ✅ 썸네일 교체
        if (imageRequest.getNewThumbnailImage() != null) {
            String newThumbnailPath = fileUploadService.uploadSingleImage(imageRequest.getNewThumbnailImage(), "productPost/Thumbnail", postId);
            post.setThumbnailImage(newThumbnailPath);
        }

        // ✅ 기존 상세 이미지 삭제
        if (imageRequest.getDeleteContentImageIds() != null) {
            for (String path : imageRequest.getDeleteContentImageIds()) {
                // 예: DB에 이미지 엔티티가 따로 없다면 아래 작업 생략 가능
                // productDescriptionImageRepository.deleteByPath(path);
            }
            // 기존 content에서 이미지 태그 제거 (선택적으로 구현)
        }

        // ✅ 새로운 상세 이미지 추가
        StringBuilder contentWithImages = new StringBuilder(request.getContent());
        if (imageRequest.getNewContentImages() != null) {
            List<String> newContentPaths = fileUploadService.uploadMultiImages(imageRequest.getNewContentImages(), "productPost/content", postId);
            for (String url : newContentPaths) {
                contentWithImages.append("<br><img src='").append(url).append("'/>");
            }
        }

        // ✅ 반드시 본문 최종 저장
        post.setContent(contentWithImages.toString());

        // ✅ 기존 상품 일부 삭제
        if (deleteProductImageIds != null && !deleteProductImageIds.isEmpty()) {
            productRepository.deleteAllByIdInBatch(deleteProductImageIds);
        }

// ✅ 상품 목록 + 이미지 저장
        List<ProductEntity> products = new ArrayList<>();
        if (request.getProducts() != null && !request.getProducts().isEmpty()) {

            // 이미지가 전달되었는지 확인하고 저장
            List<String> productImagePaths = new ArrayList<>();
            if (newProductImages != null && !newProductImages.isEmpty()) {
                productImagePaths = fileUploadService.uploadMultiImages(newProductImages, "productPost/product", postId);
            }

            // 상품 생성 및 이미지 경로 매핑
            int index = 0;
            for (ProductRequest productRequest : request.getProducts()) {
                ProductEntity productEntity = productConverter.toEntity(post, productRequest);
                // 이미지 경로가 있는 경우 매핑
                if (index < productImagePaths.size()) {
                    productEntity.setImage(productImagePaths.get(index)); // image 필드 존재 필요
                }
                products.add(productEntity);
                index++;
            }

            productRepository.saveAll(products);         // 상품 저장
        }


        Category category = categoryRepository.findById(request.getCategoryId()).orElse(null);

        // ✅ 기타 게시글 정보 수정
        post.updateFromRequest(request, category);
        ProductPostEntity saveEntity = productPostRepository.save(post);

        PostDetailResponse detailResponse = productPostConverter.detailToResponse(productRepository.findByProductPostEntity(saveEntity), productDeliveryRepository.findByProductPostEntity(saveEntity), saveEntity);

        return ResponseEntity.ok(detailResponse);
    }


    // 상품글, 상품, 배달지의 정보 조회
    public ResponseEntity<PostDetailResponse> detailProductPost(Long id) {

        ProductPostEntity entity = productPostRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("해당 상품글이 존재하지 않습니다."));

        // 레디스로 조회수 증가
        productPostViewService.increaseViewCount(id);

//        // 조회수 증가
//        entity.increaseViews();
//        productPostRepository.save(entity);

        List<ProductEntity> products = productRepository.findByProductPostEntity(entity);

        List<ProductDeliveryEntity> delivers = productDeliveryRepository.findByProductPostEntity(entity);

        PostDetailResponse response = productPostConverter.detailToResponse(products, delivers, entity);

        return ResponseEntity.ok(response);
    }

    // 상품글 삭제
    public ResponseEntity<String> deleteProductPost(@AuthenticationPrincipal UserEntity user, Long id) {

        ProductPostEntity entity = productPostRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("해당 상품글이 존재하지 않습니다."));

        // 삭제를 요청한 유저와 판매자의 유저의 정보가 일치하는지 확인
        if (!entity.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).body("본인이 아닙니다."); // 권한 체크
        }
        // 삭제
        productPostRepository.delete(entity);

        return ResponseEntity.ok("성공적으로 삭제 되었습니다.");
    }

    // 상품글 리스트
    public ResponseEntity<Page<PostsResponse>> getProductPostList(Pageable pageable) {
        Page<ProductPostEntity> postPage = productPostRepository.findAll(pageable);
        Page<PostsResponse> responsePage = postPage.map(productPostConverter::toPostsResponse); // PostResponse로 변환

        return ResponseEntity.ok(responsePage);
    }
}
