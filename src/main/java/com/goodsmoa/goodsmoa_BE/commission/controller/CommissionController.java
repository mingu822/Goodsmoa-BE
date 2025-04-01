package com.goodsmoa.goodsmoa_BE.commission.controller;

import com.goodsmoa.goodsmoa_BE.commission.dto.detail.CommissionDetailRequest;
import com.goodsmoa.goodsmoa_BE.commission.dto.detail.CommissionDetailResponse;
import com.goodsmoa.goodsmoa_BE.commission.dto.post.*;
import com.goodsmoa.goodsmoa_BE.commission.service.CommissionService;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/commission")
public class CommissionController {

    private final CommissionService service;

    // 임시로 글을 저장해야 신청양식을 추가시킬 수 있음
    @PostMapping("/post-save")
    public ResponseEntity<SavePostResponse> SaveCommissionPost(@AuthenticationPrincipal UserEntity user, @RequestBody SavePostRequest request){
        return service.saveCommissionPost(user,request);
    }

    // 커미션 신청 양식 추가하기
    @PostMapping("/detail-create")
    public ResponseEntity<CommissionDetailResponse> createCommissionDetail(@RequestBody CommissionDetailRequest request){
        return service.createCommissionDetail(request);
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
