package com.goodsmoa.goodsmoa_BE.demand.service;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.json.JsonData;
import com.goodsmoa.goodsmoa_BE.demand.converter.DemandPostSearchConverter;
import com.goodsmoa.goodsmoa_BE.demand.entity.DemandPostDocument;
import com.goodsmoa.goodsmoa_BE.demand.entity.DemandPostEntity;
import com.goodsmoa.goodsmoa_BE.demand.repository.DemandPostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class DemandPostSearchService {
    private final ElasticsearchOperations elasticsearchOperations;
    private final DemandPostSearchConverter demandPostSearchConverter;
    private final DemandPostRepository demandPostRepository;

    // 색인 추가/수정
    public void saveOrUpdateDocument(DemandPostEntity demandPostEntity) {
        elasticsearchOperations.save(demandPostSearchConverter.toDocument(demandPostEntity));
    }

    // 끌어올림 수정
    public void updatePulledAt(Long id) {
        LocalDateTime lastPulledAt = findSearchDocumentByIdWithThrow(id).getPulledAt();
        LocalDateTime fiveDaysAgo = LocalDateTime.now().minusDays(5);

        if (lastPulledAt.isAfter(fiveDaysAgo)) {
            throw new IllegalStateException("최근 5일 이내에 이미 끌어올림을 했습니다. 다음 가능 일자: "
                    + lastPulledAt.plusDays(5).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        }
        Document updateDoc = Document.from(Collections.singletonMap("pulled_at", LocalDateTime.now()));
        UpdateQuery updateQuery = UpdateQuery.builder(id.toString())
                .withDocument(updateDoc)
                .build();
        elasticsearchOperations.update(updateQuery, IndexCoordinates.of("demand_post_entity"));
    }

    // 공개/비공개 수정
    public void updateState(Long id, boolean state) {
        Document updateDoc = Document.from(Collections.singletonMap("state", state));
        UpdateQuery updateQuery = UpdateQuery.builder(id.toString())
                .withDocument(updateDoc)
                .build();
        elasticsearchOperations.update(updateQuery, IndexCoordinates.of("demand_post_entity"));
    }

    // 색인 삭제
    public void deletePostDocument(Long id) {
        elasticsearchOperations.delete(id.toString(), DemandPostDocument.class);
    }

    // 키워드로 검색하기
    public SearchHits<DemandPostDocument> search(String keyword,
                                                 Integer category,
                                                 String sortBy,
                                                 boolean includeExpired,
                                                 boolean includeScheduled,
                                                 boolean excludePrivate,
                                                 int page) {

        boolean hasKeyword = keyword != null && !keyword.isEmpty();
        boolean hasCategory = category != null && category != 0;

        // 제목/내용/해시태그/닉네임 키워드로 검색 + 카테고리 필터
        Query query;
        if (!hasKeyword && !hasCategory && includeExpired && includeScheduled && !excludePrivate) {
            // 전체 조회
            query = Query.of(q -> q.matchAll(ma -> ma));
        } else {
            List<Query> mustQueries = new ArrayList<>();
            List<Query> mustNotQueries = new ArrayList<>();
            List<Query> shouldQueries = new ArrayList<>();
            List<Query> filterQueries = new ArrayList<>();

            // 카테고리 조건
            if (hasCategory) {
                filterQueries.add(Query.of(q -> q.term(t -> t.field("category_id").value(category))));
            }
            // 키워드 조건 (title, content, hashtag, nick_name 중 하나라도 match)
            if (hasKeyword) {
//                List<String> tokens = noriAnalyzerService.analyzeWithNori(keyword);
//                mustQueries.add(Query.of(q -> q.multiMatch(m -> m
//                        .fields("title", "content", "hashtag", "nick_name")
//                        .query(keyword)
//                        .operator(Operator.And)
//                )));
                List<String> tokens = Arrays.stream(keyword.trim().split(" ")).toList();
                if (tokens.size() > 1) {
                    for (String token : tokens) {
                        mustQueries.add(Query.of(q -> q
                                .multiMatch(m -> m
                                        .fields("title", "content", "hashtag", "nickname")
                                        .query(token)
                                )
                        ));
                    }
                }else {
                    // 단일 토큰일 경우 기존처럼 should에 추가
                    shouldQueries.add(Query.of(q -> q
                            .multiMatch(m -> m
                                    .fields("title", "content", "hashtag", "nickname")
                                    .query(keyword)
                            )
                    ));
                }
                shouldQueries.add(Query.of(q -> q
                        .multiMatch(m -> m
                                .fields("title.ngram", "content.ngram", "hashtag.ngram", "nickname.ngram")
                                .query(keyword)
                        )
                ));
            }
            // 비공개 필터링
            if (excludePrivate) {
                mustNotQueries.add(Query.of(q -> q.term(t -> t
                        .field("isState")
                        .value(false)
                )));
            }
            String now = String.valueOf(Instant.now().toEpochMilli());
            // 마감글 필터링
            if (!includeExpired) {
                filterQueries.add(Query.of(q -> q.range(r -> r
                        .untyped(u -> u
                                .field("end_time")
                                .gte(JsonData.of(now))
                        )
                )));
            }
            // 예정글 필터링
            if (!includeScheduled) {
                filterQueries.add(Query.of(q -> q.range(r -> r
                        .untyped(u -> u
                                .field("start_time")
                                .lte(JsonData.of(now))
                        )
                )));
            }

            // 키워드, 카테고리, 필터 없으면 전체조회
            BoolQuery.Builder boolBuilder = new BoolQuery.Builder()
                    .must(mustQueries)
                    .mustNot(mustNotQueries)
                    .should(shouldQueries)
                    .filter(filterQueries);

            if (!shouldQueries.isEmpty() && mustQueries.isEmpty() && filterQueries.isEmpty() && mustNotQueries.isEmpty()) {
                boolBuilder.minimumShouldMatch("1");
            }
            query = Query.of(q -> q.bool(boolBuilder.build()));
        }

        // 정렬조건 추가
        Sort sort;
        switch (sortBy) {
            case "old":
                sort = Sort.by(Sort.Direction.ASC, "pulled_at");
                break;
            case "close":
                sort = Sort.by(Sort.Direction.ASC, "end_time");
                break;
            case "new":
            default:
                sort = Sort.by(Sort.Direction.DESC, "pulled_at");
                break;
        }

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(query)
                .withSort(sort)
                .withPageable(PageRequest.of(page, 10))
                .build();
        System.out.println(query.toString());
        System.out.println(sort.toString());
        return elasticsearchOperations.search(nativeQuery, DemandPostDocument.class);
    }

    // demand_post_entity 색인된 모든것 삭제
    public void deleteAllIndex() {
        elasticsearchOperations.delete("*", DemandPostEntity.class);
    }

    // 모든 데이터 색인
    @Transactional
    public void indexAllData() {
        // DB에서 모든 데이터를 가져옵니다.
        Iterable<DemandPostEntity> allDemandPosts = demandPostRepository.findAll();

        // 모든 데이터를 Elasticsearch에 색인
        for (DemandPostEntity demandPostEntity : allDemandPosts) {
            elasticsearchOperations.save(demandPostSearchConverter.toDocument(demandPostEntity));
        }
        log.info("All data has been indexed.");
    }

    private DemandPostDocument findSearchDocumentByIdWithThrow(Long id){
        return Optional.ofNullable(elasticsearchOperations.get(id.toString(), DemandPostDocument.class))
                .orElseThrow(() -> new EntityNotFoundException("Document not found"));
    }
}
