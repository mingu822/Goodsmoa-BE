package com.goodsmoa.goodsmoa_BE.elasticsearch.Repository;

import com.goodsmoa.goodsmoa_BE.elasticsearch.Document.TradePostDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface TradePostSearchRepository extends ElasticsearchRepository<TradePostDocument, Long> {
    List<TradePostDocument> findByTitleContainingOrHashtagContainingOrContentContaining(String title, String hashtag, String content);
}

