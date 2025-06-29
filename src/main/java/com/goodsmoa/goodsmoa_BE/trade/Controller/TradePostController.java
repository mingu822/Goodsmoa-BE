package com.goodsmoa.goodsmoa_BE.trade.Controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.goodsmoa.goodsmoa_BE.category.Entity.Category;
import com.goodsmoa.goodsmoa_BE.enums.Board;
import com.goodsmoa.goodsmoa_BE.search.dto.SearchDocWithUserResponse;
import com.goodsmoa.goodsmoa_BE.search.service.SearchService;
import com.goodsmoa.goodsmoa_BE.trade.Converter.TradeImageUpdateConverter;
import com.goodsmoa.goodsmoa_BE.trade.DTO.Image.TradeImageRequest;
import com.goodsmoa.goodsmoa_BE.trade.DTO.Post.*;
import com.goodsmoa.goodsmoa_BE.trade.Entity.TradeImageEntity;
import com.goodsmoa.goodsmoa_BE.trade.Service.TradeLikeService;
import com.goodsmoa.goodsmoa_BE.trade.Service.TradePostService;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/tradePost")
@RequiredArgsConstructor
public class TradePostController {

    private final TradePostService tradePostService;
    private final TradeLikeService tradeLikeService;
    private final SearchService searchService;
    // 중고거래 글 작성
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TradePostResponse> createTradePost(
            @AuthenticationPrincipal UserEntity user,
            @RequestPart("request") TradePostRequest request,
            @RequestPart(value = "thumbnailImage", required = false) MultipartFile thumbnailImage,
            @RequestPart(value = "contentImages", required = false) List<MultipartFile> contentImages,
            @RequestPart(value = "productImages", required = false) List<MultipartFile> productImages)
    {
        // ✅ 리모델링된 TradeImageRequest를 사용!
        TradeImageRequest imageRequest = TradeImageRequest.builder()
                .thumbnailImage(thumbnailImage)
                .contentImages(contentImages)
                .productImages(productImages)
                .build();

        // 서비스 메서드 시그니처도 TradeImageRequest를 받도록 되어 있으면 됨
        return tradePostService.createTradePost(user, request, imageRequest);
    }

    /**
     * 게시글 수정 API (최종 버전)
     */
    @PostMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TradePostUpdateResponse> updateTradePost(
            @AuthenticationPrincipal UserEntity user,
            @PathVariable Long id,
            @RequestPart("request") TradePostRequest request, // ✅ 이제 모든 텍스트 데이터를 이 DTO 하나로 받음!
            @RequestPart(value = "newThumbnailImage", required = false) MultipartFile newThumbnailImage,
            @RequestPart(value = "newContentImages", required = false) List<MultipartFile> newContentImages,
            @RequestPart(value = "newProductImages", required = false) List<MultipartFile> newProductImages
    ) {
        // 이미지 관련 데이터를 TradeImageUpdateRequest DTO로 묶어줌
        TradeImageUpdateRequest imageRequest = TradeImageUpdateConverter.toUpdate(
                newThumbnailImage,
                newContentImages,
                newProductImages,
                request.getDeleteProductImageIds()
        );

        return tradePostService.updateTradePost(user, id, request, imageRequest);
    }


    // 중고거래 글 삭제
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteTradePost(
            @AuthenticationPrincipal UserEntity user,
            @PathVariable Long id) {
        return tradePostService.deleteTradePost(user, id);
    }

    // 중고거래 글 조회
    @GetMapping("/{id}")
    public ResponseEntity<TradePostDetailResponse> getTradePost(@PathVariable Long id) {
        return tradePostService.getTradePost( id);
    }
    // 중고거래 끌어올림
    @PutMapping("/pull/{id}")
    public ResponseEntity<TradePostPulledResponse> pullTradePost(@PathVariable Long id) {
        return tradePostService.pullPost(id);
    }

    //중고거래 리스트 조회
    @GetMapping("/post")
    public ResponseEntity<Page<TradePostLookResponse>> getTradePostList(
            @AuthenticationPrincipal UserEntity user,
            @PageableDefault(size = 10 , sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable){
        return tradePostService.getTradePostList(user,pageable);
    }
    @GetMapping
    public ResponseEntity<Page<SearchDocWithUserResponse>> findTradePosts(
            @RequestParam(defaultValue = "ALL", name = "search_type") String searchType,
            @RequestParam Optional<String> query,
            @RequestParam Optional<Integer> category,
            @RequestParam(defaultValue = "new", name = "order_by") String orderBy,
            @RequestParam(required = false, defaultValue = "false", name = "include_expired")  boolean includeExpired,
            @RequestParam(required = false, defaultValue = "false", name = "include_scheduled")  boolean includeScheduled,
            @RequestParam(defaultValue = "0", name = "page") int page,
            @RequestParam(defaultValue = "0", name = "page_size") int pageSize,
            @AuthenticationPrincipal UserEntity user
            )
    {
        Page<SearchDocWithUserResponse> result = searchService.detailedSearch(
                searchType,
                query.orElse(null),
                Board.TRADE,
                category.orElse(0),
                orderBy,
                includeExpired,
                includeScheduled,
                page,
                pageSize
        );

        if(user!=null) tradeLikeService.addLikeStatus(user, result.getContent());

        return ResponseEntity.ok(result);
    }
    @PatchMapping("/status/{id}")
    public ResponseEntity<TradeStatusUpdateResponse> updateTradeStatus(
            @PathVariable Long id,
            @RequestBody TradeStatusUpdateRequest request) {

        // 1. 서비스 호출해서 로직 처리하고, 결과 DTO를 받음
        TradeStatusUpdateResponse responseDto = tradePostService.updateTradeStatus(id, request.getTradeStatus());

        // 2. 성공 응답(200 OK)과 함께, 변경된 상태 정보가 담긴 DTO를 body에 넣어 반환
        return ResponseEntity.ok(responseDto);
    }


}

