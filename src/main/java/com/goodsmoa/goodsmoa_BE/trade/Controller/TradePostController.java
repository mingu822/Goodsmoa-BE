package com.goodsmoa.goodsmoa_BE.trade.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goodsmoa.goodsmoa_BE.category.Entity.Category;
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

@RestController
@RequestMapping("/tradePost")
@RequiredArgsConstructor
public class TradePostController {

    private final TradePostService tradePostService;

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

}

