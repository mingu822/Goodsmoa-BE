package com.goodsmoa.goodsmoa_BE.demand.controller;

import com.goodsmoa.goodsmoa_BE.demand.dto.order.DemandOrderCreateRequest;
import com.goodsmoa.goodsmoa_BE.demand.dto.order.DemandOrderListResponse;
import com.goodsmoa.goodsmoa_BE.demand.dto.order.DemandOrderResponse;
import com.goodsmoa.goodsmoa_BE.demand.dto.order.DemandOrderUpdateRequest;
import com.goodsmoa.goodsmoa_BE.demand.dto.post.DemandSearchRequest;
import com.goodsmoa.goodsmoa_BE.demand.service.DemandOrderService;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/demand/order")
@RequiredArgsConstructor
public class DemandOrderApiController {

    private final DemandOrderService demandOrderService;

    // 유저의 모든 주문 목록 조회
//    @GetMapping
//    public ResponseEntity<List<DemandOrderResponse>> findAll(@AuthenticationPrincipal UserEntity user){
//        return ResponseEntity.ok(demandOrderService.getDemandOrderList(user));
//    }
    @GetMapping
    public ResponseEntity<Page<DemandOrderResponse>> findAll(
            @AuthenticationPrincipal UserEntity user,
            @RequestParam Optional<Integer> category,
            @RequestParam(defaultValue = "0", name = "page") int page,
            @RequestParam(defaultValue = "10", name = "page_size") int pageSize
    ){
        DemandSearchRequest searchRequest = new DemandSearchRequest(category.orElse(0), page, pageSize);
        return ResponseEntity.ok(demandOrderService.getDemandOrderList(user, searchRequest));
    }

    // 수요조사 주문 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<DemandOrderResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(demandOrderService.getDemandOrderResponse(id));
    }
    
    // 수요조사 주문 생성
    @PostMapping("/create/{id}")
    public ResponseEntity<DemandOrderResponse> create(@AuthenticationPrincipal UserEntity user,
                                                     @PathVariable Long id,
                                                     @RequestBody DemandOrderCreateRequest request) {
        return ResponseEntity.ok(demandOrderService.createDemandOrder(user, id, request));
    }

    // 수요조사 주문 수정
    @PutMapping("/update/{id}")
    public ResponseEntity<DemandOrderResponse> update(@AuthenticationPrincipal UserEntity user,
                                                   @PathVariable Long id,
                                                   @RequestBody DemandOrderUpdateRequest request){
        return ResponseEntity.ok(demandOrderService.updateDemandOrder(user, id, request));
    }

    // 수요조사 주문 삭제
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@AuthenticationPrincipal UserEntity user,
                                         @PathVariable Long id){
        return ResponseEntity.ok(demandOrderService.deleteDemandOrder(user, id));
    }
}