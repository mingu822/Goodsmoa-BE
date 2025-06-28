package com.goodsmoa.goodsmoa_BE.bestseller.service;

import com.goodsmoa.goodsmoa_BE.enums.Board;
import com.goodsmoa.goodsmoa_BE.search.document.SearchDocument;
import com.goodsmoa.goodsmoa_BE.search.dto.SearchDocWithUserResponse;
import com.goodsmoa.goodsmoa_BE.search.service.SearchService;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import com.goodsmoa.goodsmoa_BE.user.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BestsellerService {

    private final SearchService searchService;
    private final UserRepository userRepository;

    public List<SearchDocWithUserResponse> getTop5RankedByScore(String type) {
        // 1. 도메인 매핑
        Board board = switch (type.toLowerCase()) {
            case "demand" -> Board.DEMAND;
            case "product" -> Board.PRODUCT;
            case "trade" -> Board.TRADE;
            default -> throw new IllegalArgumentException("Invalid type: " + type);
        };

        // 2. Elasticsearch에서 해당 게시판의 views 내림차순 top 20 문서 가져오기
        List<SearchDocument> docs = searchService.getTopViewedDocuments(board, 20);

        // 3. userId 모아서 유저정보 미리 조회
        Set<String> userIds = docs.stream()
                .map(SearchDocument::getUserId)
                .collect(Collectors.toSet());

        Map<String, UserEntity> userMap = userRepository.findAllById(userIds)
                .stream()
                .collect(Collectors.toMap(UserEntity::getId, Function.identity()));

        // 4. score 계산 및 내림차순 정렬 후 상위 5개만 리턴
        return docs.stream()
                .map(doc -> {
                    double score = calculateScore(doc.getViews(), doc.getPulledAt());
                    UserEntity user = userMap.get(doc.getUserId());
                    return new DocWithScore(doc, user, score);
                })
                .sorted(Comparator.comparingDouble(DocWithScore::getScore).reversed())
                .limit(5)
                .map(DocWithScore::toResponse)
                .toList();
    }

    private double calculateScore(Long views, LocalDateTime pulledAt) {
        long minutes = Duration.between(pulledAt, LocalDateTime.now()).toMinutes();
        return views / (minutes <= 0 ? 1.0 : (double) minutes); // 최소 1분 방어
    }

    private record DocWithScore(SearchDocument doc, UserEntity user, double score) {
        public SearchDocWithUserResponse toResponse() {
            return SearchDocWithUserResponse.builder()
                    .id(doc.getId())
                    .boardType(doc.getBoardType().name())
                    .title(doc.getTitle())
                    .hashtag(doc.getHashtag())
                    .thumbnailUrl(doc.getThumbnailUrl())
                    .views(doc.getViews())
                    .likes(doc.getLikes())
                    .endTime(doc.getEndTime())
                    .nickname(user != null ? user.getNickname() : "알 수 없음")
                    .profileUrl(user != null ? user.getImage() : null)
                    .build();
        }

        public double getScore() {
            return score;
        }
    }
}
