package com.goodsmoa.goodsmoa_BE.trade.Service;



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
import org.springframework.data.domain.Page;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class TradeLikeService {
    private final TradeLikeRepository tradeLikeRepository;
    private final TradePostRepository tradePostRepository;
    private final TradeLikeConverter tradeLikeConverter;
    private final TradePostConverter tradePostConverter;
    @Transactional
    public ResponseEntity<TradeLikeResponse> likeTrade(UserEntity user, Long tradeId){
        TradePostEntity tradePostEntity = tradePostRepository.findById(tradeId).orElseThrow(()-> new IllegalArgumentException("해당 글이 존재하지 않습니다."));


        if (tradeLikeRepository.existsByTradeAndUser(tradePostEntity, user)){
            throw new IllegalArgumentException("이미 찜한 상품입니다.");
        }
        TradeLikeEntity like =  tradeLikeConverter.toEntity(tradePostEntity, user);

        tradeLikeRepository.save(like);

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


}
