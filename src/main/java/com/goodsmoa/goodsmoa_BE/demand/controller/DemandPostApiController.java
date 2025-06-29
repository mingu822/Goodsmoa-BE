package com.goodsmoa.goodsmoa_BE.demand.controller;

import com.goodsmoa.goodsmoa_BE.demand.service.DemandLikeService;
import com.goodsmoa.goodsmoa_BE.enums.Board;
import com.goodsmoa.goodsmoa_BE.demand.dto.post.*;
import com.goodsmoa.goodsmoa_BE.demand.service.DemandPostService;
import com.goodsmoa.goodsmoa_BE.enums.SearchType;
import com.goodsmoa.goodsmoa_BE.search.dto.SearchDocWithUserResponse;
import com.goodsmoa.goodsmoa_BE.search.service.SearchService;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/demand")
@RequiredArgsConstructor
public class DemandPostApiController {

    private final SearchService searchService;
    private final DemandPostService demandPostService;
    private final DemandLikeService demandLikeService;

    // 키워드 검색
    @GetMapping
    public ResponseEntity<Page<SearchDocWithUserResponse>> findByKeyword
    (
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
                Board.DEMAND,
                category.orElse(0),
                orderBy,
                includeExpired,
                includeScheduled,
                page,
                pageSize
        );

        if(user!=null) demandLikeService.addLikeStatus(user, result.getContent());

        return ResponseEntity.ok(result);
    }

    // 로그인한 유저가 작성한 글 목록
    @GetMapping("/user")
    public ResponseEntity<Page<DemandPostResponse>> findByUser(
            @AuthenticationPrincipal UserEntity user,
            @RequestParam Optional<Integer> category,
            @RequestParam(defaultValue = "0", name = "page") int page,
            @RequestParam(defaultValue = "10", name = "page_size") int pageSize
    ) {
        DemandSearchRequest searchRequest = new DemandSearchRequest(category.orElse(0), page, pageSize);
        return ResponseEntity.ok(demandPostService.getDemandPostListByUser(user, searchRequest));
    }

    // 수요조사 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<DemandPostResponse> findById(@AuthenticationPrincipal UserEntity user,
                                                       @PathVariable Long id) {
        if(user==null) {
            return ResponseEntity.ok(demandPostService.getDemandPostResponse(id));
        }
        else{
            return ResponseEntity.ok(demandPostService.getDemandPostResponse(user, id));
        }
    }
    
    // 수요조사 글 작성
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> create(
            @AuthenticationPrincipal UserEntity user,
            @RequestPart("demandPostCreateRequest") DemandPostCreateRequest request,
            @RequestPart("thumbnailImage") MultipartFile thumbnailImage,
            @RequestPart(value = "productImages", required = false) List<MultipartFile> productImages,
            @RequestPart(value = "descriptionImages", required = false) List<MultipartFile> descriptionImages)  throws IOException
    {
        if(user==null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다");
        return ResponseEntity.ok(demandPostService.createDemand(user, request, thumbnailImage, productImages, descriptionImages));
    }

    // 수요조사 글 수정
    @PutMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DemandPostResponse> update(
            @AuthenticationPrincipal UserEntity user,
            @PathVariable Long id,
            @RequestPart("demandPostUpdateRequest") DemandPostUpdateRequest request,
            @RequestPart(value = "newThumbnailImage", required = false) MultipartFile newThumbnailImage,
            @RequestPart(value = "newProductImages", required = false) List<MultipartFile> newProductImages,
            @RequestPart(value = "newDescriptionImages", required = false) List<MultipartFile> newDescriptionImages) throws IOException
    {
        return ResponseEntity.ok(demandPostService.updateDemand(
                user, id, request,
                newThumbnailImage,
                newProductImages,
                newDescriptionImages)
        );
    }

    // 수요조사 글 삭제
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@AuthenticationPrincipal UserEntity user,
                                         @PathVariable Long id){
        return ResponseEntity.ok(demandPostService.deleteDemand(user, id));
    }

    // 끌어올림
    @PostMapping("/pull/{id}")
    public ResponseEntity<String> pull(@AuthenticationPrincipal UserEntity user,
                                       @PathVariable Long id){
        try {
            demandPostService.pullDemand(user, id);
            return ResponseEntity.ok("글을 끌어올렸습니다");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(e.getMessage());
        }
    }

    @PostMapping("/convert/{id}")
    public ResponseEntity<DemandPostToSaleResponse> convertToProduct(
            @AuthenticationPrincipal UserEntity user,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(demandPostService.convertToProduct(id, user));
    }

    @GetMapping("/reIndex")
    public ResponseEntity<String> reIndexing(){
        demandPostService.indexAllData();
        return ResponseEntity.ok("기존 인덱스를 삭제하고 새로 인덱싱 했습니다.");
    }
}
