package com.goodsmoa.goodsmoa_BE.demand.controller;

import com.goodsmoa.goodsmoa_BE.demand.dto.post.DemandPostCreateRequest;
import com.goodsmoa.goodsmoa_BE.demand.dto.post.DemandPostListResponse;
import com.goodsmoa.goodsmoa_BE.demand.dto.post.DemandPostResponse;
import com.goodsmoa.goodsmoa_BE.demand.dto.post.DemandPostUpdateRequest;
import com.goodsmoa.goodsmoa_BE.demand.service.DemandPostService;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/demand")
@RequiredArgsConstructor
public class DemandApiController {

    private final DemandPostService demandPostService;

    // 모든 수요조사 조회
    @GetMapping
    public ResponseEntity<List<DemandPostListResponse>> findAll(){
        return ResponseEntity.ok(demandPostService.getDemandEntityList());
    }

    // 수요조사 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<DemandPostResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(demandPostService.getDemandPostResponse(id));
    }
    
    // 수요조사 글 작성
    @PostMapping("/create")
    public ResponseEntity<DemandPostResponse> create(@AuthenticationPrincipal UserEntity user,
                                                 @RequestBody DemandPostCreateRequest request) {
        return ResponseEntity.ok(demandPostService.createDemand(user, request));
    }

    // 수요조사 글 수정
    @PutMapping("/update")
    public ResponseEntity<DemandPostResponse> update(@AuthenticationPrincipal UserEntity user,
                                                   @RequestBody DemandPostUpdateRequest request){
        return ResponseEntity.ok(demandPostService.updateDemand(user, request));
    }

    // 수요조사 글 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<String> delete(@AuthenticationPrincipal UserEntity user, Long id){
        return ResponseEntity.ok(demandPostService.deleteDemand(user, id));
    }
}
