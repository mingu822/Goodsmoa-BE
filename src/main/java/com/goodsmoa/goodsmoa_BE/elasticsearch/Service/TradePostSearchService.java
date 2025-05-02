package com.goodsmoa.goodsmoa_BE.elasticsearch.Service;

import com.goodsmoa.goodsmoa_BE.elasticsearch.Document.TradePostDocument;
import com.goodsmoa.goodsmoa_BE.elasticsearch.Document.TradePostDocumentConverter;
import com.goodsmoa.goodsmoa_BE.elasticsearch.Repository.TradePostSearchRepository;
import com.goodsmoa.goodsmoa_BE.trade.Converter.TradePostConverter;
import com.goodsmoa.goodsmoa_BE.trade.DTO.Post.TradePostDetailResponse;
import com.goodsmoa.goodsmoa_BE.trade.DTO.Post.TradePostRequest;
import com.goodsmoa.goodsmoa_BE.trade.DTO.Post.TradePostResponse;
import com.goodsmoa.goodsmoa_BE.trade.Entity.TradePostEntity;
import com.goodsmoa.goodsmoa_BE.trade.Repository.TradePostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TradePostSearchService {

    private final TradePostSearchRepository searchRepository;
    private final TradePostRepository tradePostRepository; // JPA Repository
    private final TradePostDocumentConverter tradePostDocumentConverter;
    private final TradePostConverter tradePostConverter;

    // 검색 수행
    public List<TradePostDetailResponse> search(String keyword) {
        List<TradePostDocument> docs = searchRepository
                .findByTitleContainingOrHashtagContainingOrContentContaining(keyword, keyword, keyword);

        return docs.stream()
                .map(doc -> tradePostRepository.findById(doc.getId()).orElse(null))
                .filter(Objects::nonNull)
                .map(tradePostConverter::detailResponse)
                .collect(Collectors.toList());
    }

    // 저장: DB 저장 후 Elasticsearch에도 저장
    public void savePost(TradePostEntity post) {
        TradePostDocument document = tradePostDocumentConverter.toSearchDocument(post);
        searchRepository.save(document);// Elasticsearch 저장
    }
}