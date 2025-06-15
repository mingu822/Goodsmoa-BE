package com.goodsmoa.goodsmoa_BE.trade.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goodsmoa.goodsmoa_BE.category.Entity.Category;
import com.goodsmoa.goodsmoa_BE.enums.Board;
import com.goodsmoa.goodsmoa_BE.search.dto.SearchDocWithUserResponse;
import com.goodsmoa.goodsmoa_BE.search.service.SearchService;
import com.goodsmoa.goodsmoa_BE.trade.Converter.TradeImageUpdateConverter;
import com.goodsmoa.goodsmoa_BE.trade.DTO.Image.TradeImageRequest;
import com.goodsmoa.goodsmoa_BE.trade.DTO.Post.*;
import com.goodsmoa.goodsmoa_BE.trade.Entity.TradeImageEntity;
import com.goodsmoa.goodsmoa_BE.trade.Service.TradePostService;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tradePost")
@RequiredArgsConstructor
public class TradePostController {

    private final TradePostService tradePostService;
    private final SearchService searchService;
    // 중고거래 글 작성
    @PostMapping(value="/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TradePostResponse> createTradePost(
            @AuthenticationPrincipal UserEntity user,
            @RequestPart("request") TradePostRequest request,
            @RequestPart(value = "thumbnailImage", required = false) MultipartFile thumbnailImage,
            @RequestPart(value = "contentImages", required = false) List<MultipartFile> contentImages,
            @RequestPart(value = "productImages", required = false) List<MultipartFile> productImages)
    {
        TradeImageRequest imageRequest = TradeImageRequest.builder()
                .thumbnailImage(thumbnailImage)
                .contentImages(contentImages)
                .productImages(productImages)
                .build();
        return tradePostService.createTradePost(user, request,imageRequest);
    }

    @PostMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TradePostUpdateResponse> updateTradePost(
            @AuthenticationPrincipal UserEntity user,
            @PathVariable Long id,
            @RequestPart("request") String requestJson,
            @RequestPart(value = "newThumbnailImage", required = false) MultipartFile newThumbnailImage,
            @RequestPart(value = "newContentImages", required = false) List<MultipartFile> newContentImages,
            @RequestPart(value = "newProductImages", required = false) List<MultipartFile> newProductImages,
            @RequestPart(value = "deleteContentImageIds", required = false) List<String> deleteContentImageIds,
            @RequestParam(value = "deleteProductImageIds", required = false) List<Long> deleteProductImageIds // <- 변경
    ) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            TradePostRequest request = objectMapper.readValue(requestJson, TradePostRequest.class);

            TradeImageUpdateRequest imageRequest = TradeImageUpdateConverter.toUpdate(
                    newThumbnailImage,
                    newContentImages,
                    newProductImages,
                    deleteContentImageIds,
                    deleteProductImageIds
            );

            return tradePostService.updateTradePost(user, id, request, imageRequest);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
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
            @RequestParam Optional<String> query,
            @RequestParam Optional<Integer> category,
            @RequestParam(defaultValue = "new", name = "order_by") String orderBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size)
    {
        // 'close' 정렬 옵션은 중고거래에 의미가 없으므로 'new'로 처리
        if ("close".equals(orderBy)) {
            orderBy = "new";
        }

        return ResponseEntity.ok(
                searchService.detailedSearch(
                        query.orElse(null),
                        Board.TRADE, // ✨ Board 타입을 TRADE로 고정
                        category.orElse(0),
                        orderBy,
                        false, // ✨ includeExpired는 false로 고정
                        false, // ✨ includeScheduled는 false로 고정
                        page,
                        size
                )
        );
    }

}

