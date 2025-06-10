package com.goodsmoa.goodsmoa_BE.search.controller;

import com.goodsmoa.goodsmoa_BE.enums.Board;
import com.goodsmoa.goodsmoa_BE.search.dto.SearchDocWithUserResponse;
import com.goodsmoa.goodsmoa_BE.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
    @GetMapping
    public ResponseEntity<Map<Board, List<SearchDocWithUserResponse>>> findByKeyword(@RequestParam Optional<String> query,
                                                                                     @RequestParam Optional<Integer> category,
                                                                                     @RequestParam(defaultValue = "new", name = "order_by") String orderBy,
                                                                                     @RequestParam(required = false, defaultValue = "false", name = "include_expired")  boolean includeExpired,
                                                                                     @RequestParam(required = false, defaultValue = "false", name = "include_scheduled")  boolean includeScheduled,
                                                                                     @RequestParam(name = "page_size") Optional<Integer> pageSize){
        return ResponseEntity.ok(
                searchService.integratedSearch(
                        query.orElse(null),
                        category.orElse(0),
                        orderBy,
                        includeExpired,
                        includeScheduled,
                        pageSize.orElse(8)
                )
        );
    }
}
