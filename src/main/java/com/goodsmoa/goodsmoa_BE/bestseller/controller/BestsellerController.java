package com.goodsmoa.goodsmoa_BE.bestseller.controller;

import com.goodsmoa.goodsmoa_BE.bestseller.service.BestsellerService;
import com.goodsmoa.goodsmoa_BE.search.dto.SearchDocWithUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bestseller")
@RequiredArgsConstructor
public class BestsellerController {

    private final BestsellerService bestsellerService;

    @GetMapping
    public List<SearchDocWithUserResponse> getBestsellerDocs(@RequestParam("type") String type) {
        return bestsellerService.getTop5RankedByScore(type);
    }
}
