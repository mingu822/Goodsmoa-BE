package com.goodsmoa.goodsmoa_BE.search.controller;

import com.goodsmoa.goodsmoa_BE.enums.Board;
import com.goodsmoa.goodsmoa_BE.search.dto.SearchDocWithUserResponse;
import com.goodsmoa.goodsmoa_BE.search.service.SearchService;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchApiController {

    private final SearchService searchService;

    // 키워드 검색
//    @GetMapping
//    public ResponseEntity<Map<Board, List<SearchDocWithUserResponse>>> findByKeyword(
//            @RequestParam(defaultValue = "ALL", name = "search_type") String searchType,
//            @RequestParam Optional<String> query,
//            @RequestParam Optional<Integer> category,
//            @RequestParam(defaultValue = "new", name = "order_by") String orderBy,
//            @RequestParam(required = false, defaultValue = "false", name = "include_expired")  boolean includeExpired,
//            @RequestParam(required = false, defaultValue = "false", name = "include_scheduled")  boolean includeScheduled,
//            @RequestParam(name = "page_size") Optional<Integer> pageSize)
//    {
//        return ResponseEntity.ok(
//                searchService.integratedSearch(
//                        searchType,
//                        query.orElse(null),
//                        category.orElse(0),
//                        orderBy,
//                        includeExpired,
//                        includeScheduled,
//                        pageSize.orElse(8)
//                )
//        );
//    }

    //통합검색
    @GetMapping
    public ResponseEntity<Page<SearchDocWithUserResponse>> findByKeyword
            (
                    @RequestParam(defaultValue = "ALL", name = "search_type") String searchType,
                    @RequestParam(defaultValue = "TRADE", name = "board_type") String boardType,
                    @RequestParam Optional<String> query,
                    @RequestParam(defaultValue = "0", name = "page") int page
            )
    {
        try {
            Board board = Board.valueOf(boardType);
            Page<SearchDocWithUserResponse> result = searchService.detailedSearch(
                    searchType,
                    query.orElse(null),
                    board,
                    0,
                    "new",
                    false,
                    true,
                    page,
                    25
            );
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            log.warn("존재하지 않는 게시판 타입입니다: {}", boardType);
            return ResponseEntity.badRequest().build();
        }
    }
}
