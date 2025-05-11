package com.goodsmoa.goodsmoa_BE.trade.Controller;

import com.goodsmoa.goodsmoa_BE.category.Entity.Category;
import com.goodsmoa.goodsmoa_BE.trade.DTO.Image.TradeImageRequest;
import com.goodsmoa.goodsmoa_BE.trade.DTO.Post.*;
import com.goodsmoa.goodsmoa_BE.trade.Entity.TradeImageEntity;
import com.goodsmoa.goodsmoa_BE.trade.Service.TradePostService;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
        TradeImageRequest imageRequest = new TradeImageRequest();
        imageRequest.setThumbnailImage(thumbnailImage);
        imageRequest.setContentImages(contentImages);
        imageRequest.setProductImages(productImages);
        return tradePostService.createTradePost(user, request,imageRequest);
    }

    // 중고거래 글에 이미지 추가
//    @PostMapping("/{id}/image/add")
//    public ResponseEntity<String> addImage(
//            @PathVariable Long id,
//            @RequestBody TradeImageRequest imageRequests) {
//        tradePostService.addImage(id, imageRequests);
//        return ResponseEntity.ok("이미지 추가 완료");
//    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TradePostUpdateResponse> updateTradePost(
            @AuthenticationPrincipal UserEntity user,
            @PathVariable Long id,
            @RequestPart("request") TradePostRequest request,
            @RequestPart(value = "imageRequest", required = false) TradeImageUpdateRequest imageRequest
    ) {
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

    @PutMapping("/pull/{id}")
    public ResponseEntity<TradePostPulledResponse> pullTradePost(@PathVariable Long id) {
        return tradePostService.pullPost(id);
    }
}

