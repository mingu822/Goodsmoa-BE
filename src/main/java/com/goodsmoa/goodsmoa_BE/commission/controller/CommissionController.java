package com.goodsmoa.goodsmoa_BE.commission.controller;

import com.goodsmoa.goodsmoa_BE.commission.dto.detail.CommissionDetailRequest;
import com.goodsmoa.goodsmoa_BE.commission.dto.detail.CommissionDetailResponse;
import com.goodsmoa.goodsmoa_BE.commission.dto.post.*;
import com.goodsmoa.goodsmoa_BE.commission.service.CommissionService;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/commission")
public class CommissionController {

    private final CommissionService service;

    // 커미션 신청 양식 추가하기
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostResponse> createCommissionDetail(
            @AuthenticationPrincipal UserEntity user,
            @RequestPart("postRequest")PostRequest request,
            @RequestPart("thumbnailImage") MultipartFile thumbnailImage,
            @RequestPart(value = "contentImages", required = false) List<MultipartFile> contentImages) throws IOException{
        return service.createCommissionDetail(user,request,thumbnailImage,contentImages);
    }

    // 커미션 신청글 완성 및 수정
    @PutMapping("/post-create")
    public ResponseEntity<PostResponse> createCommissionPost(@AuthenticationPrincipal UserEntity user, @RequestBody PostRequest request){
        return service.updateCommissionPost(user,request);
    }

    @GetMapping("/post-detail/{id}")
    public ResponseEntity<PostDetailResponse> detailCommissionPost(@PathVariable Long id){
        return service.detailCommissionPost(id);
    }

    //커미션 글 삭제
    @DeleteMapping("/post-delete/{id}")
    public ResponseEntity<String> deleteCommissionPost(@AuthenticationPrincipal UserEntity user, @PathVariable Long id){
        return service.deleteCommissionPost(user,id);
    }

}
