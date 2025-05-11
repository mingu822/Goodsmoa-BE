package com.goodsmoa.goodsmoa_BE.elasticsearch.Controller;

import com.goodsmoa.goodsmoa_BE.elasticsearch.Service.TradePostSearchService;
import com.goodsmoa.goodsmoa_BE.trade.DTO.Post.TradePostDetailResponse;
import com.goodsmoa.goodsmoa_BE.trade.DTO.Post.TradePostRequest;
import com.goodsmoa.goodsmoa_BE.trade.DTO.Post.TradePostResponse;
import com.goodsmoa.goodsmoa_BE.trade.Entity.TradePostEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/search")
public class TradePostSearchController {

    private final TradePostSearchService searchService;

    @GetMapping
    public List<TradePostDetailResponse> search(@RequestParam String keyword)
    {
        return searchService.search(keyword);
    }
}

