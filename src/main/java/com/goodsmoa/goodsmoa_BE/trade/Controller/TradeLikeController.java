package com.goodsmoa.goodsmoa_BE.trade.Controller;

import com.goodsmoa.goodsmoa_BE.trade.DTO.Like.TradeLikeRequest;
import com.goodsmoa.goodsmoa_BE.trade.DTO.Like.TradeLikeResponse;
import com.goodsmoa.goodsmoa_BE.trade.Service.TradeLikeService;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/trade-like")
public class TradeLikeController {

    private final TradeLikeService tradeLikeService;
    @PostMapping("/like/{tradeId}")
    public ResponseEntity<TradeLikeResponse> likeTrade(
            @AuthenticationPrincipal UserEntity user,
            @PathVariable Long tradeId
    ) {
        return tradeLikeService.likeTrade(user, tradeId);
    }

    @DeleteMapping("/{tradeId}")
    public ResponseEntity<Void> unlikeTrade(
            @AuthenticationPrincipal UserEntity user,
            @PathVariable Long tradeId
    ) {
        return tradeLikeService.unlikeTrade(user, tradeId);
    }
//    @GetMapping
//    public ResponseEntity<List<TradeLikeResponse>> getLikedTrades(
//            @AuthenticationPrincipal UserEntity user
//    ) {
//        return tradeLikeService.getLikedTrades(user);
//    }
    @GetMapping("/likes")
    public ResponseEntity<Page<TradeLikeResponse>> getLikedPosts(
            @AuthenticationPrincipal UserEntity user,
            @PageableDefault(size = 10, sort = "createAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return tradeLikeService.getPagedLiked(user, pageable);
    }

}
