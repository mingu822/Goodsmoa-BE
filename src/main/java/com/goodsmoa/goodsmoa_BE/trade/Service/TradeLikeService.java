package com.goodsmoa.goodsmoa_BE.trade.Service;



import com.goodsmoa.goodsmoa_BE.search.dto.SearchDocWithUserResponse;
import com.goodsmoa.goodsmoa_BE.trade.Converter.TradeLikeConverter;
import com.goodsmoa.goodsmoa_BE.trade.Converter.TradePostConverter;
import com.goodsmoa.goodsmoa_BE.trade.DTO.Like.TradeLikeRequest;
import com.goodsmoa.goodsmoa_BE.trade.DTO.Like.TradeLikeResponse;
import com.goodsmoa.goodsmoa_BE.trade.Entity.TradeLikeEntity;
import com.goodsmoa.goodsmoa_BE.trade.Entity.TradePostEntity;
import com.goodsmoa.goodsmoa_BE.trade.Repository.TradeLikeRepository;
import com.goodsmoa.goodsmoa_BE.trade.Repository.TradePostRepository;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TradeLikeService {
    private final TradeLikeRepository tradeLikeRepository;
    private final TradePostRepository tradePostRepository;
    private final TradeLikeConverter tradeLikeConverter;
    private final TradePostConverter tradePostConverter;
    private final TradeRedisService tradeRedisService;

    @Transactional
    public ResponseEntity<TradeLikeResponse> likeTrade(UserEntity user, Long tradeId){
        TradePostEntity tradePostEntity = tradePostRepository.findById(tradeId).orElseThrow(()-> new IllegalArgumentException("해당 글이 존재하지 않습니다."));


        if (tradeLikeRepository.existsByTradeAndUser(tradePostEntity, user)){
            throw new IllegalArgumentException("이미 찜한 상품입니다.");
        }
        TradeLikeEntity like =  tradeLikeConverter.toEntity(tradePostEntity, user);

        tradeLikeRepository.save(like);

        //  Redis 좋아요 수 증가(es 재색인용)
        tradeRedisService.increaseLikeCount(tradeId);

        TradeLikeResponse response = tradeLikeConverter.toResponse(like);
        return ResponseEntity.ok(response);
    }
    @Transactional
    public ResponseEntity<Void> unlikeTrade(UserEntity user, Long tradeId) {
        TradePostEntity trade = tradePostRepository.findById(tradeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 거래글이 존재하지 않습니다."));

        TradeLikeEntity like = tradeLikeRepository.findByTradeAndUser(trade, user)
                .orElseThrow(() -> new IllegalArgumentException("찜한 기록이 없습니다."));

        tradeLikeRepository.delete(like);
        tradeRedisService.decreaseLikeCount(tradeId);
        return ResponseEntity.noContent().build();
    }
//    public ResponseEntity<List<TradeLikeResponse>> getLikedTrades(UserEntity user) {
//        List<TradeLikeResponse> liked = tradeLikeRepository.findAllByUser(user).stream()
//                .map(TradeLikeConverter::toResponse)
//                .collect(Collectors.toList());
//        return ResponseEntity.ok(liked);
//    }

    @Transactional
    public ResponseEntity<Page<TradeLikeResponse>> getPagedLiked(UserEntity user, Pageable pageable) {
        Page<TradeLikeEntity> likedPage = tradeLikeRepository.findAllByUser(user, pageable);

        Page<TradeLikeResponse> responsePage = likedPage.map(tradeLikeConverter::toResponse);

        return ResponseEntity.ok(responsePage);
    }
    @Transactional
    public TradeLikeResponse getSingleLiked(UserEntity user, Long tradeId) {
        // 1. 좋아요한 게시글(TradePostEntity)을 먼저 찾습니다.
        TradePostEntity tradePost = tradePostRepository.findById(tradeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Trade post not found with ID: " + tradeId));

        // 2. 찾아낸 TradePostEntity와 UserEntity를 사용하여 TradeLikeEntity를 조회합니다.
        TradeLikeEntity tradeLikeEntity = tradeLikeRepository.findByTradeAndUser(tradePost, user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Trade like not found for user and trade ID: " + tradeId));

        // 3. TradeLikeEntity를 TradeLikeResponse DTO로 변환하여 반환합니다.
        return tradeLikeConverter.toResponse(tradeLikeEntity);
    }
    public Map<Long, Boolean> getMyLikesStatusForPosts(String userId, List<Long> postIds) {
        // 1. Repository를 통해, 사용자가 '좋아요' 누른 게시글의 ID 목록만 효율적으로 가져옵니다.
        List<Long> likedPostIdsFromDb = tradeLikeRepository.findLikedPostIdsByUserIdAndPostIdsIn(userId, postIds);
        Set<Long> likedPostIdSet = new HashSet<>(likedPostIdsFromDb);

        // 2. 프론트가 요청한 모든 게시글 ID를 순회하며, '좋아요' 여부를 Map에 담습니다.
        Map<Long, Boolean> result = new HashMap<>();
        for (Long postId : postIds) {
            // '좋아요' 누른 ID 목록(Set)에 포함되어 있으면 true, 아니면 false를 할당
            result.put(postId, likedPostIdSet.contains(postId));
        }

        return result;
    }

    public void addLikeStatus(UserEntity user, List<SearchDocWithUserResponse> responses) {
        // 1. 응답에서 숫자 ID 추출 (예: "DEMAND_16" → 16)
        List<Long> numericPostIds = new ArrayList<>();
        Map<Long, SearchDocWithUserResponse> idToResponseMap = new HashMap<>();

        for (SearchDocWithUserResponse res : responses) {
            try {
                // ID 형식: "BOARD_ID"
                String[] parts = res.getId().split("_");
                if (parts.length >= 2) {
                    Long numericId = Long.parseLong(parts[1]);
                    numericPostIds.add(numericId);
                    idToResponseMap.put(numericId, res);
                }
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                log.warn("Invalid ID format: {}", res.getId());
            }
        }
        // 2. 숫자 ID로 좋아요 여부 일괄 조회
        if (!numericPostIds.isEmpty()) {
            Set<Long> likedNumericIds = tradeLikeRepository.findLikedIdsByUserAndPosts(
                    user.getId(), numericPostIds
            );

            // 3. 응답 객체에 좋아요 여부 매핑
            idToResponseMap.forEach((numericId, response) -> {
                response.setLiked(likedNumericIds.contains(numericId));
            });
        }
    }


}
