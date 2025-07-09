package com.goodsmoa.goodsmoa_BE.commission.controller;

import com.goodsmoa.goodsmoa_BE.commission.dto.apply.SubscriptionRequest;
import com.goodsmoa.goodsmoa_BE.commission.dto.post.*;
import com.goodsmoa.goodsmoa_BE.commission.service.CommissionLikeService;
import com.goodsmoa.goodsmoa_BE.commission.service.CommissionService;
import com.goodsmoa.goodsmoa_BE.enums.Board;
import com.goodsmoa.goodsmoa_BE.search.dto.SearchDocWithUserResponse;
import com.goodsmoa.goodsmoa_BE.search.service.SearchService;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
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

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/commission")
public class CommissionController {

    private final CommissionService service;
    private final SearchService searchService;
    private final CommissionLikeService commissionLikeService;

    // 커미션 생성
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostResponse> createCommissionDetail(
            @AuthenticationPrincipal UserEntity user,
            @RequestPart("postRequest")PostRequest request,
            @RequestPart("thumbnailImage") MultipartFile thumbnailImage,
            @RequestPart(value = "contentImages", required = false) List<MultipartFile> contentImages) throws IOException{
        return service.createCommissionDetail(user,request,thumbnailImage,contentImages);
    }

    // 커미션 수정
    @PutMapping("/update")
    public ResponseEntity<PostResponse> createCommissionPost(
            @AuthenticationPrincipal UserEntity user,
            @RequestPart("postRequest") PostRequest request,
            @RequestPart(value = "newThumbnailImage", required = false) MultipartFile newThumbnailImage,
            @RequestPart(value = "newContentImages", required = false) List<MultipartFile> newContentImages,
            @RequestPart(value = "deleteDetailIds", required = false) String deleteDetailIdsJson) throws IOException{
        return service.updateCommissionPost(user,request,newThumbnailImage,newContentImages,deleteDetailIdsJson);
    }

    // 키워드 검색
    @GetMapping("/search")
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
        Page<SearchDocWithUserResponse> result =searchService.detailedSearch(
                searchType,
                query.orElse(null),
                Board.COMMISSION,
                category.orElse(0),
                orderBy,
                includeExpired,
                includeScheduled,
                page,
                pageSize
        );

        if(user != null) commissionLikeService.addLikeStatus(user, result.getContent());

        return ResponseEntity.ok(result);
    }

    // 커미션 상세 조회
    @GetMapping("/post-detail/{id}")
    public ResponseEntity<PostDetailResponse> detailCommissionPost(@PathVariable Long id){
        return service.detailCommissionPost(id);
    }

    // 내가 쓴 커미션 글 가져오기
    @GetMapping("/post")
    public ResponseEntity<Page<PostResponse>> findUserCommissionPosts(
            @AuthenticationPrincipal UserEntity user,
            @PageableDefault(size = 10 , sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable){
        return service.findUserCommissionPosts(user,pageable);
    }

    //커미션 글 삭제
    @DeleteMapping("/post-delete/{id}")
    public ResponseEntity<String> deleteCommissionPost(@AuthenticationPrincipal UserEntity user, @PathVariable Long id){
        return service.deleteCommissionPost(user,id);
    }

    // 커미션 신청
    // todo 신청 후에 어디로 넘어갈지 결정하고 필요한 값 넘기기
    @PostMapping("/subscription")
    public ResponseEntity<String> subscriptionCommissionPost(
            @AuthenticationPrincipal UserEntity user,
            @RequestPart("subscriptionRequest") List<SubscriptionRequest> request,
            @RequestPart(value = "contentImages", required = false) List<MultipartFile> contentImages) throws IOException {
        return service.subscriptionCommissionPost(user, request, contentImages);
    }

    // 커미션 신청 수정
    // todo 신청 후에 어디로 넘어갈지 결정하고 필요한 값 넘기기
    @PutMapping("/subscription")
    public ResponseEntity<String> subscriptionUpdate (
            @AuthenticationPrincipal UserEntity user,
            @RequestPart("subscriptionRequest") List<SubscriptionRequest> request,
            @RequestPart(value = "contentImages", required = false) List<MultipartFile> contentImages) throws IOException {
        return service.subscriptionCommissionPost(user, request, contentImages);
    }



}