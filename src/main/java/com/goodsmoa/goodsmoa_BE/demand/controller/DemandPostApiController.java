package com.goodsmoa.goodsmoa_BE.demand.controller;

import com.goodsmoa.goodsmoa_BE.enums.Board;
import com.goodsmoa.goodsmoa_BE.demand.dto.post.*;
import com.goodsmoa.goodsmoa_BE.demand.service.DemandPostService;
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

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/demand")
@RequiredArgsConstructor
public class DemandPostApiController {

    private final SearchService searchService;
    private final DemandPostService demandPostService;

    // 키워드 검색
    @GetMapping
    public ResponseEntity<Page<SearchDocWithUserResponse>> findByKeyword
    (
            @RequestParam Optional<String> query,
            @RequestParam Optional<Integer> category,
            @RequestParam(defaultValue = "new", name = "order_by") String orderBy,
            @RequestParam(required = false, defaultValue = "false", name = "include_expired")  boolean includeExpired,
            @RequestParam(required = false, defaultValue = "false", name = "include_scheduled")  boolean includeScheduled,
            @RequestParam(defaultValue = "0", name = "page") int page,
            @RequestParam(defaultValue = "0", name = "page_size") int pageSize
    )
    {
        return ResponseEntity.ok(
                searchService.detailedSearch(
                        query.orElse(null),
                        Board.DEMAND,
                        category.orElse(0),
                        orderBy,
                        includeExpired,
                        includeScheduled,
                        page,
                        pageSize
                )
        );
    }

    // 수요조사 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<DemandPostResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(demandPostService.getDemandPostResponse(id));
    }
    
    // 수요조사 글 작성
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> create(
            @AuthenticationPrincipal UserEntity user,
            @RequestPart("demandPostCreateRequest") DemandPostCreateRequest request,
            @RequestPart("thumbnailImage") MultipartFile thumbnailImage,
            @RequestPart(value = "productImages", required = false) List<MultipartFile> productImages,
            @RequestPart(value = "descriptionImages", required = false) List<MultipartFile> descriptionImages
    ) {
        if(user==null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다");
        return ResponseEntity.ok(demandPostService.createDemand(user, request, thumbnailImage, productImages, descriptionImages));
    }
//    @PostMapping("/create")
//    public ResponseEntity<DemandPostResponse> create(@AuthenticationPrincipal UserEntity user,
//                                                     @RequestBody DemandPostCreateRequest request) {
//        return ResponseEntity.ok(demandPostService.createDemand(user, request));
//    }

    // 수요조사 글 수정
    @PutMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DemandPostResponse> update(
            @AuthenticationPrincipal UserEntity user,
            @PathVariable Long id,
            @RequestPart("demandPostUpdateRequest") DemandPostUpdateRequest request,
            @RequestPart(value = "newThumbnailImage", required = false) MultipartFile newThumbnailImage,
            @RequestPart(value = "newProductImages", required = false) List<MultipartFile> newProductImages,
            @RequestPart(value = "newDescriptionImages", required = false) List<MultipartFile> newDescriptionImages
    ){
        return ResponseEntity.ok(demandPostService.updateDemand(
                user, id, request,
                newThumbnailImage,
                newProductImages,
                newDescriptionImages)
        );
    }
//    @PutMapping("/update")
//    public ResponseEntity<DemandPostResponse> update(@AuthenticationPrincipal UserEntity user,
//                                                   @RequestBody DemandPostUpdateRequest request){
//        return ResponseEntity.ok(demandPostService.updateDemand(user, request));
//    }

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
        return ResponseEntity.ok(demandPostService.pullDemand(user, id));
    }

    @GetMapping("/reIndex")
    public ResponseEntity<String> reIndexing(){
        demandPostService.indexAllData();
        return ResponseEntity.ok("기존 인덱스를 삭제하고 새로 인덱싱 했습니다.");
    }
}
