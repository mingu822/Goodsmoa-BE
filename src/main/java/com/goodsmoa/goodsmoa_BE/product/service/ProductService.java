package com.goodsmoa.goodsmoa_BE.product.service;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.goodsmoa.goodsmoa_BE.config.S3Uploader;
import com.goodsmoa.goodsmoa_BE.product.dto.delivery.ProductDeliveryRequest;
import com.goodsmoa.goodsmoa_BE.product.entity.*;
import com.goodsmoa.goodsmoa_BE.product.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.goodsmoa.goodsmoa_BE.category.Entity.Category;
import com.goodsmoa.goodsmoa_BE.category.Repository.CategoryRepository;
import com.goodsmoa.goodsmoa_BE.fileUpload.FileUploadService;
import com.goodsmoa.goodsmoa_BE.product.converter.ProductConverter;
import com.goodsmoa.goodsmoa_BE.product.converter.ProductDeliveryConverter;
import com.goodsmoa.goodsmoa_BE.product.converter.ProductImageUpdateConverter;
import com.goodsmoa.goodsmoa_BE.product.converter.ProductPostConverter;
import com.goodsmoa.goodsmoa_BE.product.dto.ProductRequest;
import com.goodsmoa.goodsmoa_BE.product.dto.Image.ProductImageUpdateRequest;
import com.goodsmoa.goodsmoa_BE.product.dto.post.PostDetailResponse;
import com.goodsmoa.goodsmoa_BE.product.dto.post.PostRequest;
import com.goodsmoa.goodsmoa_BE.product.dto.post.PostsResponse;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
    private final ProductLikeRepository productLikeRepository;
    private final ProductReviewRepository productReviewRepository;
    private final S3Uploader s3Uploader;


    // 상품글 생성
    @Transactional
    public ResponseEntity<PostDetailResponse> createPost(
            UserEntity user,
            PostRequest request,
            MultipartFile thumbnailImage,
            List<MultipartFile> productImages,
            List<MultipartFile> contentImages) throws IOException {

        // 게시글 저장을 위한 카테고리 찾기
        Category category = categoryRepository.getReferenceById(request.getCategoryId());

        // 게시글 생성 및 저장 (썸네일은 아직 없음)
        ProductPostEntity entity = productPostConverter.createToEntity(request, user, category);
        ProductPostEntity saveEntity = productPostRepository.save(entity);

        Long postId = saveEntity.getId();

        // 썸네일 이미지 저장
        String thumbnailPath = s3Uploader.upload(thumbnailImage);
//        String thumbnailPath = fileUploadService.uploadSingleImage(thumbnailImage, "productPost/thumbnail", postId);
        saveEntity.setThumbnailImage(thumbnailPath);
        productPostRepository.save(saveEntity);

        // 상품 목록 + 이미지 저장
        List<ProductEntity> products = new ArrayList<>();
        if (request.getProducts() != null && !request.getProducts().isEmpty()) {
            List<String> productImagePaths = new ArrayList<>();
            if (productImages != null && !productImages.isEmpty()) {
                productImagePaths = fileUploadService.uploadMultiImages(productImages, "productPost/product", postId);
            }

            int index = 0;
            for (ProductRequest productRequest : request.getProducts()) {
                ProductEntity productEntity = productConverter.toEntity(saveEntity, productRequest);
                if (index < productImagePaths.size()) {
                    productEntity.setImage(productImagePaths.get(index));
                }
                products.add(productEntity);
                index++;
            }
            productRepository.saveAll(products);
            saveEntity.setState(true);
            productPostRepository.save(saveEntity);
        }

        // 상세 이미지 저장 및 본문에 삽입
        if (contentImages != null && !contentImages.isEmpty()) {
            List<String> contentImagePaths = fileUploadService.uploadMultiImages(contentImages, "productPost/content",
                    postId);

            String originalContent = request.getContent();

            Pattern pattern = Pattern.compile("src=[\"'](.*?)[\"']");
            Matcher matcher = pattern.matcher(originalContent);

            int i = 0;
            StringBuffer result = new StringBuffer();

            while (matcher.find() && i < contentImagePaths.size()) {
                String newPath = "src='" + contentImagePaths.get(i++) + "'";
                matcher.appendReplacement(result, newPath);
            }
            matcher.appendTail(result);

            saveEntity.setContent(result.toString());
            productPostRepository.save(saveEntity);
        }

        // 배달 옵션 저장
        List<ProductDeliveryEntity> delivers = new ArrayList<>();
        if (request.getDelivers() != null && !request.getDelivers().isEmpty()) {
            delivers = request.getDelivers().stream()
                    .map(productDeliveryRequest -> productDeliveryConverter.toEntity(productDeliveryRequest,
                            saveEntity))
                    .toList();
            productDeliveryRepository.saveAll(delivers);
        }

        PostDetailResponse response = productPostConverter.detailToResponse(products, delivers, saveEntity);
        return ResponseEntity.ok(response);
    }

    // 상품글 업데이트
    @Transactional
    public ResponseEntity<PostDetailResponse> updateProductPost(
            UserEntity user,
            Long postId,
            PostRequest request,
            MultipartFile newThumbnailImage,
            List<MultipartFile> newContentImages,
            List<MultipartFile> newProductImages,
            String deleteProductImageIdsJson,
            String deleteDeliveryIds) {

        List<Long> deleteProductIds;
        try {
            deleteProductIds = new ObjectMapper().readValue(
                    deleteProductImageIdsJson,
                    new TypeReference<>() {}
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        List<Long> deleteDelivers;
        try {
            deleteDelivers = new ObjectMapper().readValue(
                    deleteDeliveryIds,
                    new TypeReference<>() {}
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        // 이미지 DTO로 변환
        ProductImageUpdateRequest imageRequest = productImageUpdateConverter.toUpdate(
                newThumbnailImage, newContentImages, newProductImages,
                deleteProductIds);

        // 게시글 조회
        ProductPostEntity post = productPostRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다."));

        // 권한 확인
        if (!user.getId().equals(post.getUser().getId())) {
            throw new UnsupportedOperationException("작성자만 수정할 수 있습니다.");
        }

        // ✅ 썸네일 이미지 교체
        if (imageRequest.getNewThumbnailImage() != null) {
            String oldThumbnailPath = post.getThumbnailImage();
            if (oldThumbnailPath != null && !oldThumbnailPath.isEmpty()) {
                // 기존 썸네일 이미지 삭제
                log.info("oldThumbnailPath : {}", oldThumbnailPath);
                fileUploadService.deleteImage(oldThumbnailPath);
            }
            String newThumbnailPath = fileUploadService.uploadSingleImage(
                    imageRequest.getNewThumbnailImage(), "productPost/thumbnail", postId);
            log.info("newThumbnailPath : {}",newThumbnailPath);
            post.setThumbnailImage(newThumbnailPath);
        }

        // ✅ 상세설명 이미지 경로 교체 및 저장
        if (imageRequest.getNewContentImages() != null && !imageRequest.getNewContentImages().isEmpty()) {

            String originalContent = post.getContent(); // 기존 저장된 content 기준
            Pattern pattern = Pattern.compile("src=['\"](productPost/content/[^'\"]+)['\"]");
            Matcher matcher = pattern.matcher(originalContent);

            while (matcher.find()) {
                String imagePath = matcher.group(1); // ex: productPost/content/2_1.png
                fileUploadService.deleteImage(imagePath); // ✅ 삭제 수행
            }

            List<String> newContentPaths = fileUploadService.uploadMultiImages(
                    imageRequest.getNewContentImages(), "productPost/content", postId);

            String newContent = request.getContent();
            // <img src='...'> 패턴 정규식
            pattern = Pattern.compile("src=[\"'](.*?)[\"']");
            matcher = pattern.matcher(newContent);

            int i = 0;
            StringBuilder result = new StringBuilder();

            while (matcher.find() && i < newContentPaths.size()) {
                String newPath = "src='" + newContentPaths.get(i++) + "'";
                matcher.appendReplacement(result, newPath);
            }
            matcher.appendTail(result);

            post.setContent(result.toString());
            productPostRepository.save(post);
        } else {
            // 이미지가 없더라도 텍스트만 업데이트
            post.setContent(request.getContent());
            productPostRepository.save(post);
        }

        // 상품 업데이트
        List<ProductEntity> newProducts = new ArrayList<>();
        List<MultipartFile> newImages = imageRequest.getNewProductImages();
        int imageIndex = 0;

        for (ProductRequest productRequest : request.getProducts()) {
            log.info("productRequest.getName() : "+productRequest.getName());
            log.info("productRequest.isImageUpdated() : "+productRequest.isImageUpdated());
            if (productRequest.getId() != null && productRequest.getId() > 0) {
                // 기존 상품
                ProductEntity existingProduct = productRepository.findById(productRequest.getId())
                        .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다: " + productRequest.getId()));

                existingProduct.updateFromRequest(productRequest);

                if (productRequest.isImageUpdated()) {
                    MultipartFile image = newImages.get(imageIndex++);

                    // 기존 이미지 삭제
                    fileUploadService.deleteImage(existingProduct.getImage());

                    // ✅ 기존 상품 ID 기준 파일 저장
                    String fileName = postId + "_" + productRequest.getId();
                    String savedPath = fileUploadService.uploadImageWithCustomName(image, "productPost/product", fileName);

                    existingProduct.setImage(savedPath);
                }

            } else {
                // 신규 상품
                ProductEntity newProduct = productConverter.toEntity(post, productRequest);

                // 먼저 저장해서 ID 확보
                newProduct = productRepository.save(newProduct);

                if (productRequest.isImageUpdated()) {
                    MultipartFile image = newImages.get(imageIndex++);

                    // ✅ 방금 생성된 상품의 ID로 저장
                    String fileName = postId + "_" + newProduct.getId();
                    String savedPath = fileUploadService.uploadImageWithCustomName(image, "productPost/product", fileName);

                    newProduct.setImage(savedPath);
                }

                newProducts.add(newProduct);
            }
        }

        // ✅ 신규 상품 중 이미지 경로가 있는 것만 다시 저장 (이미 ID 확보됨)
        productRepository.saveAll(newProducts);

        // ✅ Step 3: 상품 삭제 처리 (DB + 이미지 파일 삭제)
        if (imageRequest.getDeleteProductIds() != null && !imageRequest.getDeleteProductIds().isEmpty()) {
            List<Long> existingIds = request.getProducts().stream()
                    .filter(p -> p.getId() != null)
                    .map(ProductRequest::getId)
                    .collect(Collectors.toList());

            List<Long> deletableIds = imageRequest.getDeleteProductIds().stream()
                    .filter(id -> !existingIds.contains(id))
                    .collect(Collectors.toList());

            if (!deletableIds.isEmpty()) {
                // ✅ 삭제할 상품들 먼저 조회 (이미지 경로 확인을 위해)
                List<ProductEntity> productsToDelete = productRepository.findAllById(deletableIds);

                for (ProductEntity product : productsToDelete) {
                    String imagePath = product.getImage();
                    if (imagePath != null && !imagePath.isBlank() && imagePath.contains(".") && !imagePath.endsWith("/")) {
                        try {
                            fileUploadService.deleteImage(imagePath);
                        } catch (Exception e) {
                            log.warn("상품 이미지 삭제 실패: " + imagePath, e);
                        }
                    }
                }

                // ✅ DB에서 상품 삭제
                productRepository.deleteAllByIdInBatch(deletableIds);
            }
        }

        // 배달 방식 수정
        List<ProductDeliveryEntity> newDelivers = new ArrayList<>();

        for(ProductDeliveryRequest deliveryRequest : request.getDelivers()){
            if(deliveryRequest.getId() != null && deliveryRequest.getId() > 0){
                // 기존 배달 상품
                ProductDeliveryEntity existingDelivers = productDeliveryRepository.findById(deliveryRequest.getId())
                        .orElseThrow(() -> new EntityNotFoundException("배달방식을 찾을 수 없습니다: " + deliveryRequest.getId()));

                existingDelivers.updateFromRequest(deliveryRequest);
            }else{
                // 신규 상품
                ProductDeliveryEntity newDelivery = productDeliveryConverter.toEntity(deliveryRequest,post);

                newDelivers.add(newDelivery);
            }
            productDeliveryRepository.saveAll(newDelivers);
        }
        // 배달방식 삭제
        if(deleteDelivers != null && !deleteDelivers.isEmpty()){
            productDeliveryRepository.deleteAllByIdInBatch(deleteDelivers);
        }

        // ✅ 카테고리 및 기타 정보 수정
        Category category = categoryRepository.findById(request.getCategoryId()).orElse(null);
        post.updateFromRequest(request, category);
        ProductPostEntity updatedPost = productPostRepository.save(post);

        // ✅ 응답 반환
        PostDetailResponse response = productPostConverter.detailToResponse(
                productRepository.findByProductPostEntity(updatedPost),
                productDeliveryRepository.findByProductPostEntity(updatedPost),
                updatedPost);

        return ResponseEntity.ok(response);
    }

    // 상품글 상세 조회
    public ResponseEntity<PostDetailResponse> detailProductPost(Long id) {
        ProductPostEntity entity = productPostRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 상품글이 존재하지 않습니다."));

        productPostViewService.increaseViewCount(id);

        List<ProductEntity> products = productRepository.findByProductPostEntity(entity);
        List<ProductDeliveryEntity> delivers = productDeliveryRepository.findByProductPostEntity(entity);

        PostDetailResponse response = productPostConverter.detailToResponse(products, delivers, entity);
        return ResponseEntity.ok(response);
    }

    // 삭제 로직
    public ResponseEntity<String> deleteProductPost(UserEntity user, Long id) {

        ProductPostEntity entity = productPostRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 상품글이 존재하지 않습니다."));

        if (!entity.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).body("본인이 아닙니다.");
        }
        // 상품 삭제
        List<ProductEntity> products = productRepository.findByProductPostEntity(entity);
        productRepository.deleteAll(products);

        // 배달방식 삭제
        List<ProductDeliveryEntity> delviers = productDeliveryRepository.findByProductPostEntity(entity);
        productDeliveryRepository.deleteAll(delviers);

        List<ProductLikeEntity> likes = productLikeRepository.findByProductPostEntity(entity);
        productLikeRepository.deleteAll(likes);

        List<ProductReviewEntity> review = productReviewRepository.findByProductPostEntity(entity);
        productReviewRepository.deleteAll(review);

        // 관련 이미지 파일 삭제
        deleteAllImagesByPostId(id);

        // 게시글 삭제
        productPostRepository.delete(entity);

        return ResponseEntity.ok("성공적으로 삭제 되었습니다.");
    }

    private void deleteAllImagesByPostId(Long postId) {
        List<String> folders = List.of("productPost/thumbnail", "productPost/content", "productPost/product");

        for (String folder : folders) {
            String baseUploadDir = "src/main/resources/static/";

            Path dirPath = Paths.get(baseUploadDir + folder);

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath, postId + "_*")) {
                for (Path file : stream) {
                    try {
                        Files.deleteIfExists(file);
                    } catch (IOException e) {
                        log.warn("이미지 삭제 실패: " + file.toString(), e);
                    }
                }
            } catch (IOException e) {
                log.warn("폴더 접근 실패: " + dirPath.toString(), e);
            }
        }
    }

    // 상품글 리스트 조회
    public ResponseEntity<Page<PostsResponse>> getProductPostList(Pageable pageable) {
        Page<ProductPostEntity> postPage = productPostRepository.findAll(pageable);
        Page<PostsResponse> responsePage = postPage.map(productPostConverter::toPostsResponse);
        return ResponseEntity.ok(responsePage);
    }
}