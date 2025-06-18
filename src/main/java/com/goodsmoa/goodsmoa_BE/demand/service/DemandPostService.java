package com.goodsmoa.goodsmoa_BE.demand.service;

import com.goodsmoa.goodsmoa_BE.category.Entity.Category;
import com.goodsmoa.goodsmoa_BE.category.Repository.CategoryRepository;
import com.goodsmoa.goodsmoa_BE.demand.converter.DemandPostConverter;
import com.goodsmoa.goodsmoa_BE.demand.converter.DemandPostProductConverter;
import com.goodsmoa.goodsmoa_BE.demand.dto.post.*;
import com.goodsmoa.goodsmoa_BE.demand.entity.DemandOrderEntity;
import com.goodsmoa.goodsmoa_BE.demand.entity.DemandPostEntity;
import com.goodsmoa.goodsmoa_BE.demand.entity.DemandPostProductEntity;
import com.goodsmoa.goodsmoa_BE.demand.repository.DemandOrderRepository;
import com.goodsmoa.goodsmoa_BE.demand.repository.DemandPostRepository;
import com.goodsmoa.goodsmoa_BE.fileUpload.FileUploadService;
import com.goodsmoa.goodsmoa_BE.search.service.SearchService;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class DemandPostService {

    @PersistenceContext
    private EntityManager entityManager;
    private final CategoryRepository categoryRepository;
    private final DemandPostConverter demandPostConverter;
    private final DemandPostRepository demandPostRepository;
    private final DemandPostProductConverter demandPostProductConverter;
    private final DemandPostViewService demandPostViewService;
    private final SearchService searchService;
    private final FileUploadService fileUploadService;
    private final DemandOrderRepository demandOrderRepository;
    private final DemandOrderService demandOrderService;

    // 선택한 글의 id로 검색하여 가져오기
    public DemandPostResponse getDemandPostResponse(Long id) {
        DemandPostEntity postEntity = findByIdWithThrow(id);
        // 조회수 증가
        demandPostViewService.increaseViewCount(postEntity.getId());

        return demandPostConverter.toResponse(postEntity);
    }

    // 선택한 글의 id로 검색하여 가져오기
    public DemandPostResponse getDemandPostResponse(UserEntity user, Long id) {
        DemandPostEntity postEntity = findByIdWithThrow(id);
        DemandOrderEntity orderEntity = demandOrderRepository.findByDemandPostEntityAndUser(postEntity, user);
        if(orderEntity==null){
            return demandPostConverter.toResponse(postEntity);
        }
        demandOrderService.validateUserAuthorization(user.getId(), orderEntity.getUser().getId());

        // 조회수 증가
        demandPostViewService.increaseViewCount(postEntity.getId());
        return demandPostConverter.toResponse(postEntity, orderEntity.getDemandOrderProducts());
    }

    // 선택한 글의 id로 탐색하여 판매글로 전환할 데이터 보내기
    public DemandPostToSaleResponse convertToProduct(Long id, UserEntity user) {
        DemandPostEntity postEntity = findByIdWithThrow(id);
        validateUserAuthorization(user.getId(), postEntity);
        return demandPostConverter.toSaleResponse(postEntity);
    }

    // 로그인 한 유저가 작성한 글 목록
    public Page<DemandPostResponse> getDemandPostListByUser(UserEntity user, DemandSearchRequest request){
        Page<DemandPostEntity> pageResult;
        PageRequest pageRequest = PageRequest.of(
                request.getPage(),
                request.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        if(request.getCategoryId()==0){
            pageResult = demandPostRepository.findByUserId(
                    user.getId(),
                    pageRequest
            );
        }else{
            Category category = findCategoryByIdWithThrow(request.getCategoryId());
            pageResult = demandPostRepository.findByUserIdAndCategory(
                    user.getId(),
                    category,
                    pageRequest
            );
        }
        return pageResult.map(demandPostConverter::toResponse);
    }

    // 수요조사 글 생성하기
    @Transactional
    public DemandPostResponse createDemand(
            UserEntity user,
            DemandPostCreateRequest request,
            MultipartFile thumbnailImage,
            List<MultipartFile> productImages,
            List<MultipartFile> descriptionImages
    ) {
        // 1. 카테고리 조회
        Category category = findCategoryByIdWithThrow(request.getCategoryId());

        // 2. 엔티티 생성 (ID 없음)
        DemandPostEntity postEntity = demandPostConverter.toEntity(user, category, request);

        // 3. 게시글 먼저 저장하여 ID 생성
        DemandPostEntity savedPost = demandPostRepository.save(postEntity);
        Long id = savedPost.getId();

        // 4. 썸네일 이미지 업로드 및 경로 설정
        String thumbnailPath = fileUploadService.uploadSingleImage(thumbnailImage, "demandPost/thumbnail", id);
        savedPost.setImageUrl(thumbnailPath);

        // 5. 상품 이미지 업로드 및 상품 엔티티 생성
        List<String> productImagePaths = new ArrayList<>();
        if (productImages != null && !productImages.isEmpty()) {
            for(MultipartFile img : productImages){
                productImagePaths.add(fileUploadService.uploadImageWithCustomName(img, "demandPost/product", UUID.randomUUID().toString()));
            }
        }
        List<DemandPostProductEntity> products = new ArrayList<>();
        for (int i = 0; i < request.getProducts().size(); i++) {
            DemandPostProductEntity product = demandPostProductConverter.toEntity(savedPost, request.getProducts().get(i));
            if (i < productImagePaths.size()) {
                product.setImageUrl(productImagePaths.get(i));
            }
            products.add(product);
        }
        savedPost.getProducts().addAll(products);

        // 6. 본문 이미지 처리
        if (descriptionImages != null && !descriptionImages.isEmpty()) {
            List<String> descriptionImagePaths = new ArrayList<>();
            for(MultipartFile img:descriptionImages) {
                descriptionImagePaths.add(fileUploadService.uploadImageWithCustomName(img, "demandPost/description", UUID.randomUUID().toString()));
            }
            String processedDescription = processContentImages(request.getDescription(), descriptionImagePaths);
            savedPost.setDescription(processedDescription);
        }

        // 7. 검색 서비스 동기화
        searchService.saveOrUpdateDocument(postEntity);
        log.info("생성 후 색인 시작");

        return demandPostConverter.toResponse(postEntity);
    }

    // 수요조사 글 수정하기
    @Transactional
    public DemandPostResponse updateDemand
    (
            UserEntity user, Long id,
            DemandPostUpdateRequest request,
            MultipartFile newThumbnailImage,
            List<MultipartFile> newProductImages,
            List<MultipartFile> newDescriptionImages
    ) {
        // 기존글 조회 및 수정 권한 확인
        DemandPostEntity postEntity = findByIdWithThrow(id);
        validateUserAuthorization(user.getId(), postEntity);

        // 썸네일 이미지 교체
        if (newThumbnailImage != null && !newThumbnailImage.isEmpty()) {
            fileUploadService.deleteImage(postEntity.getImageUrl());
            String thumbnailPath = fileUploadService.uploadSingleImage(newThumbnailImage, "demandPost/thumbnail", id);
            postEntity.setImageUrl(thumbnailPath);
        }

        // 기존 상품 목록을 Map 으로 변환 (ID -> 상품 객체)
        Map<Long, DemandPostProductEntity> existingProductsMap = new HashMap<>();
        for (DemandPostProductEntity product : postEntity.getProducts()) {
            existingProductsMap.put(product.getId(), product);
        }
        System.out.println(existingProductsMap);

        // 요청 받은 상품 ID 목록
        Set<Long> incomingProductIds = new HashSet<>();

        // 이미지 인덱스 추적용 변수
        int productImageIndex = 0;

        // 상품 추가 또는 수정
        for (DemandPostProductRequest productRequest : request.getProducts()) {
            Long productId = productRequest.getId();

            if (productId == null) { // 신규 상품 추가
                DemandPostProductEntity postProductEntity = demandPostProductConverter.toEntity(postEntity, productRequest);
                if (newProductImages != null && !newProductImages.isEmpty() && productImageIndex < newProductImages.size()) {
                    MultipartFile imageFile = newProductImages.get(productImageIndex++);
                    String imageUrl = fileUploadService.uploadImageWithCustomName(imageFile, "demandPost/product", UUID.randomUUID().toString());
                    postProductEntity.setImageUrl(imageUrl);
                }
                entityManager.persist(postProductEntity);
            } else { // 기존 상품 수정
                incomingProductIds.add(productId);
                DemandPostProductEntity existingProduct = existingProductsMap.get(productId);
                if (existingProduct == null) {
                    throw new IllegalStateException("해당 ID를 가진 상품을 찾을 수 없습니다: " + productId);
                }
                if (productRequest.isImageUpdated()) {
                    fileUploadService.deleteImage(existingProduct.getImageUrl());
                    if (!newProductImages.isEmpty() && productImageIndex < newProductImages.size()) {
                        MultipartFile imageFile = newProductImages.get(productImageIndex++);
                        String newImageUrl = fileUploadService.uploadImageWithCustomName(imageFile, "demandPost/product", UUID.randomUUID().toString());
                        existingProduct.setImageUrl(newImageUrl);
                    }
                }
                existingProduct.DemandPostProductUpdate(
                        productRequest.getPrice(),
                        existingProduct.getImageUrl(),
                        productRequest.getTargetCount()
                );
            }
        }

        // 상품 삭제
        List<DemandPostProductEntity> productsToRemove = new ArrayList<>();
        for (DemandPostProductEntity product : postEntity.getProducts()) {
            // 요청 받은 상품 ID 리스트에 기존 상품이 없으면 삭제 대상
            if (!incomingProductIds.contains(product.getId())) {
                productsToRemove.add(product);
            }
        }

        productsToRemove.forEach(product -> {
            fileUploadService.deleteImage(product.getImageUrl());
            try {
                fileUploadService.deleteImage(product.getImageUrl());
            } catch (Exception e) {
                log.error("상품 이미지 삭제 실패: {}", product.getImageUrl(), e);
            }
            entityManager.remove(product);
        });
        postEntity.getProducts().removeAll(productsToRemove);


        // 본문 업데이트
        String originalDescription = postEntity.getDescription();
        List<String> oldImageUrls = extractImageUrls(originalDescription);
        List<String> newDescriptionPaths = new ArrayList<>();

        // 새 이미지 업로드
        if (newDescriptionImages != null && !newDescriptionImages.isEmpty()) {
            for (MultipartFile img : newDescriptionImages) {
                newDescriptionPaths.add(fileUploadService.uploadImageWithCustomName(img, "demandPost/description", UUID.randomUUID().toString()));
            }
        }
        // 새 본문의 이미지를 저장된 이름으로 변환
        String processedDescription = processContentImages(request.getDescription() ,newDescriptionPaths);

        // 새 본문에서 사용된 이미지 URL 추출
        List<String> newImageUrls = extractImageUrls(processedDescription);

        // 기존 본문에 있었지만 새 본문에는 없는 이미지만 삭제
        oldImageUrls.stream()
                .filter(url -> !newImageUrls.contains(url))
                .forEach(url ->{
                    try{
                        fileUploadService.deleteImage(url);
                    }catch (Exception e){
                        log.error("본문 이미지 삭제 실패: {}", url, e);
                    }
                });

        // 상품 목록을 제외한 필드 업데이트
        postEntity.updateDemandEntity(
                request.getTitle(),
                processedDescription,
                request.getStartTime(),
                request.getEndTime(),
                postEntity.getImageUrl(),
                request.getHashtag(),
                findCategoryByIdWithThrow(request.getCategoryId())
        );
        searchService.saveOrUpdateDocument(postEntity);
        return demandPostConverter.toResponse(postEntity);
    }

    // 수요조사 글 삭제
    public String deleteDemand(UserEntity user, Long id){
        DemandPostEntity postEntity = findByIdWithThrow(id);
        validateUserAuthorization(user.getId(), postEntity);

        List<DemandOrderEntity> list = demandOrderRepository.findByDemandPostEntity(postEntity);
        demandOrderRepository.deleteAll(list);

        demandPostRepository.delete(postEntity);
        searchService.deletePostDocument("DEMAND_"+id);
        deleteAllImagesByPostId(postEntity);

        return "글을 삭제하였습니다";
    }

    private void deleteAllImagesByPostId(DemandPostEntity postEntity) {
        try {
            // 썸네일 삭제 (단일 항목)
            fileUploadService.deleteImage(postEntity.getImageUrl());
        } catch (Exception e) {
            log.error("썸네일 삭제 실패: {}", postEntity.getImageUrl(), e);
        }

        // 본문 이미지 삭제
        List<String> deleteImgUrls = extractImageUrls(postEntity.getDescription());
        for (String imgUrl : deleteImgUrls) {
            try {
                fileUploadService.deleteImage(imgUrl);
            } catch (Exception e) {
                log.error("본문 이미지 삭제 실패: {}", imgUrl, e);
            }
        }

        // 상품 이미지 삭제
        for (DemandPostProductEntity entity : postEntity.getProducts()) {
            try {
                fileUploadService.deleteImage(entity.getImageUrl());
            } catch (Exception e) {
                log.error("상품 이미지 삭제 실패: {}", entity.getImageUrl(), e);
            }
        }
    }

    // 수요조사 글 끌어올리기
    public String pullDemand(UserEntity user, Long id) {
        DemandPostEntity postEntity = findByIdWithThrow(id);
        validateUserAuthorization(user.getId(), postEntity);
        try {
            searchService.updatePulledAt("DEMAND_"+postEntity.getId());
            return "글을 끌어올렸습니다";
        } catch (IllegalStateException e){
            return e.getMessage();
        }
    }

    // 수요조사 글 조회(Id)
    protected DemandPostEntity findByIdWithThrow(Long id){
        return demandPostRepository.findDemandPostEntityById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 수요조사는 존재하지 않습니다"));
    }

    // 카테고리 조회
    private Category findCategoryByIdWithThrow(Integer id){
        return categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리는 존재하지 않습니다"));
    }

    // 권한 조회
    private void validateUserAuthorization(String userId, DemandPostEntity entity) {
        if (!entity.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("권한이 없습니다");
        }
    }

    // 설명란 이미지 이름 추출
    private List<String> extractImageUrls(String content) {
        List<String> imageUrls = new ArrayList<>();
        Pattern pattern = Pattern.compile("src=[\"'](.*?)[\"']");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            imageUrls.add(matcher.group(1));
        }
        return imageUrls;
    }

    // 설명란 이미지 양식 지정
    private String processContentImages(String originalContent, List<String> descriptionImagePaths) {
        Pattern pattern = Pattern.compile("src=[\"'](.*?)[\"']");
        Matcher matcher = pattern.matcher(originalContent);

        int i = 0;
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            String replacement;
            if (i < descriptionImagePaths.size()) {
                replacement = "src='http://localhost:8080/" + descriptionImagePaths.get(i++) + "'";
//                replacement = "src='" + descriptionImagePaths.get(i++) + "'"; // i는 여기서 1 증가
            } else {
                replacement = "src='/default-image.jpg'";
            }
            matcher.appendReplacement(result, replacement);
        }
        matcher.appendTail(result);

        return result.toString();
    }

    @Transactional
    public void indexAllData() {
        // DB 에서 모든 데이터를 가져옵니다.
        Iterable<DemandPostEntity> allDemandPosts = demandPostRepository.findAll();

        // 모든 데이터를 Elasticsearch 에 색인
        for (DemandPostEntity demandPostEntity : allDemandPosts) {
//            searchService.saveOrUpdateDocument(demandPostEntity,Board.DEMAND);
            searchService.saveOrUpdateDocument(demandPostEntity);
        }
        log.info("All data has been indexed.");
    }
}