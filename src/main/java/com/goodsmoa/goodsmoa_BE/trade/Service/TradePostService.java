package com.goodsmoa.goodsmoa_BE.trade.Service;


import com.goodsmoa.goodsmoa_BE.category.Entity.Category;
import com.goodsmoa.goodsmoa_BE.category.Repository.CategoryRepository;
import com.goodsmoa.goodsmoa_BE.elasticsearch.Service.TradePostSearchService;
import com.goodsmoa.goodsmoa_BE.trade.Converter.TradeImageConverter;
import com.goodsmoa.goodsmoa_BE.trade.Converter.TradePostConverter;
import com.goodsmoa.goodsmoa_BE.trade.DTO.Image.TradeImageRequest;
import com.goodsmoa.goodsmoa_BE.trade.DTO.Image.TradeImageResponse;
import com.goodsmoa.goodsmoa_BE.trade.DTO.Post.*;
import com.goodsmoa.goodsmoa_BE.trade.Entity.TradeImageEntity;
import com.goodsmoa.goodsmoa_BE.trade.Entity.TradePostEntity;
import com.goodsmoa.goodsmoa_BE.trade.Repository.TradeImageRepository;
import com.goodsmoa.goodsmoa_BE.trade.Repository.TradePostRepository;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import com.goodsmoa.goodsmoa_BE.user.Repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TradePostService {
    private final TradePostRepository tradePostRepository;
    private final TradePostConverter tradePostConverter;
    private final TradeImageRepository tradeImageRepository;
    private final CategoryRepository categoryRepository;
    private final TradeImageConverter tradeImageConverter;
    private final UserRepository userRepository;
    private final TradePostSearchService tradePostSearchService;
//.
    // 중고거래 글 안에 쓸 사진 등록
    @Transactional
    public void addImage(Long postId, TradeImageRequest imageRequests) {
        TradePostEntity postEntity = tradePostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        List<TradeImageEntity> newImages = imageRequests.getImagePath()
                .stream()
                .map(imagePath -> TradeImageEntity.builder()
                        .imagePath(imagePath)
                        .tradePostEntity(postEntity)
                        .build())
                .collect(Collectors.toList());

        postEntity.addImagePath(newImages); // 기존 이미지 유지하고 새로운 이미지 추가

        tradePostRepository.save(postEntity);
    }



    //   중고거래 글 쓰기
    @Transactional
    public ResponseEntity<TradePostResponse> createTradePost( UserEntity user, TradePostRequest request) {
        Category category = categoryRepository.getReferenceById(request.getCategoryId());

        TradePostEntity tradePostEntity = tradePostConverter.toEntity(request, category, user);

        tradePostRepository.save(tradePostEntity);

        List<TradeImageEntity> tradeImageEntities = new ArrayList<>();

        if (request.getImagePath() != null && !request.getImagePath().isEmpty()) {
            tradeImageEntities = tradeImageConverter.toEntityList(request, tradePostEntity);
            tradeImageRepository.saveAll(tradeImageEntities); // 이미지 저장
        }
        TradePostResponse response = tradePostConverter.toResponse(tradePostEntity,tradeImageEntities);

        tradePostSearchService.savePost(tradePostEntity); // 엘라스틱 서치 저장

        return ResponseEntity.ok(response);
    }

    //    중고거래 글 업뎃
    @Transactional
    public ResponseEntity<TradePostUpdateResponse> updateTradePost(UserEntity user, Long tradePostEntityId , TradePostRequest request) {
        TradePostEntity tradePostEntity = tradePostRepository.findById(tradePostEntityId).orElseThrow(()-> new EntityNotFoundException("해당 거래글이 존재하지 않습니다."));
        if(!userRepository.existsById(user.getId())) {
            return ResponseEntity.notFound().build();
        }
        tradePostEntity.updatePost(request);
        tradePostEntity.updateTradeLocation(request);
        tradePostEntity.updateTradeOptions(request);

        return ResponseEntity.ok(tradePostConverter.upResponse(tradePostEntity));
    }

    @Transactional
    public ResponseEntity<TradePostPulledResponse> pullPost(Long id){
        TradePostEntity tradePostEntity = tradePostRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("해당글이 존재하지 않습니다."));
        tradePostEntity.pullAt(LocalDateTime.now());
        tradePostRepository.save(tradePostEntity);
        return ResponseEntity.ok(tradePostConverter.pulledResponse(tradePostEntity));

    }

    //    중고거래 글 삭제
    @Transactional
    public ResponseEntity<String> deleteTradePost(@AuthenticationPrincipal UserEntity user, Long id) {
        TradePostEntity tradePostEntity = tradePostRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("해당글이 존재하지 않습니다."));
        if(!tradePostEntity.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }
        tradePostRepository.deleteById(id);

        return ResponseEntity.ok("삭제가 완료되었습니다.");
    }

    //    중고거래 글 조회
    @Transactional
    public ResponseEntity<TradePostDetailResponse> getTradePost(Long id) {
        TradePostEntity tradePostEntity = tradePostRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("해당 게시글이 존재하지 않습니다."));
//        List<TradeImageEntity> images = tradeImageRepository.findAllById(id);

        tradePostEntity.increaseViews();
        tradePostRepository.save(tradePostEntity);
//        log.info(tradePostEntity.getViews().toString());



        return ResponseEntity.ok(tradePostConverter.detailResponse(tradePostEntity));
    }



}

