package com.goodsmoa.goodsmoa_BE.search.service;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.json.JsonData;
import com.goodsmoa.goodsmoa_BE.enums.Board;
import com.goodsmoa.goodsmoa_BE.enums.SearchType;
import com.goodsmoa.goodsmoa_BE.search.dto.SearchDocWithUserResponse;
import com.goodsmoa.goodsmoa_BE.search.converter.SearchConverter;
import com.goodsmoa.goodsmoa_BE.search.document.SearchDocument;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import com.goodsmoa.goodsmoa_BE.user.Repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {
    private final UserRepository userRepository;
    private final ElasticsearchOperations elasticsearchOperations;
    private final SearchConverter searchConverter;

    // 색인 추가/수정
    public <T> void saveOrUpdateDocument(T entity) {
        SearchDocument doc = searchConverter.toDocument(entity);
        String cleanedDescription = doc.getDescription()
                .replaceAll("<.*?>", " ")
                .replaceAll("\\s+", " ").trim();
        doc.setDescription(cleanedDescription);
        elasticsearchOperations.save(doc);
    }

    // 색인 삭제
    public void deletePostDocument(String id) {
        elasticsearchOperations.delete(id, SearchDocument.class);
    }

    // 끌어올림
    public void updatePulledAt(String id, LocalDateTime pulledAt) {
//        LocalDateTime lastPulledAt = findSearchDocByIdAndBoardWithThrow(id).getPulledAt();
//        LocalDateTime fiveDaysAgo = LocalDateTime.now().minusDays(5);
//
//        if (lastPulledAt.isAfter(fiveDaysAgo)) {
//            throw new IllegalStateException("최근 5일 이내에 이미 끌어올림을 했습니다. 다음 가능 일자: "
//                    + lastPulledAt.plusDays(5).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//        }

        String newPulledAt = pulledAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
        Document updateDoc = Document.from(Collections.singletonMap("pulled_at", newPulledAt));
        UpdateQuery updateQuery = UpdateQuery.builder(id)
                .withDocument(updateDoc)
                .build();
        elasticsearchOperations.update(updateQuery, IndexCoordinates.of("search_document"));
    }

    // 통합검색
//    public Map<Board, List<SearchDocWithUserResponse>> integratedSearch(
//            String searchType,
//            String keyword,
//            Integer category,
//            String orderBy,
//            boolean includeExpired,
//            boolean includeScheduled,
//            int pageSize
//    ) {
//        // 결과 저장용 Map (동기화된 Map 필요)
//        Map<Board, List<SearchDocWithUserResponse>> result = new ConcurrentHashMap<>();
//
//        // 비동기 작업 리스트
//        List<CompletableFuture<Void>> futures = Arrays.stream(Board.values())
//                .map(board -> CompletableFuture.runAsync(() -> {
//                    Page<SearchDocWithUserResponse> page = detailedSearch(
//                            searchType,
//                            keyword,
//                            board,
//                            category,
//                            orderBy,
//                            includeExpired,
//                            includeScheduled,
//                            0,
//                            pageSize
//                    );
//                    result.put(board, page.getContent());
//                }))
//                .toList();
//
//        // 모든 작업이 끝날 때까지 기다림
//        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
//
//        return result;
//    }

    // 검색(키워드 + 게시판)
    public Page<SearchDocWithUserResponse> detailedSearch(
            String searchType,
            String keyword,
            Board boardType,
            Integer category,
            String orderBy,
            boolean includeExpired,
            boolean includeScheduled,
            int page,
            int pageSize)
    {

        // 제목/내용/해시태그/닉네임 키워드로 검색 + 카테고리/게시판 필터 + 정렬
        // 1. BoolQuery 빌더 생성
        BoolQuery.Builder boolQuery = QueryBuilders.bool();

        // 2. 키워드 검색 조건
        if (StringUtils.hasText(keyword)) {
            List<String> tokens = Arrays.stream(keyword.trim().split("\\s+"))
                    .filter(token -> !token.isEmpty())
                    .toList();
            // 2-1. 토큰이 여러 개인 경우: 모든 토큰 일치 (AND)
            if (!tokens.isEmpty()) {
                for (String token : tokens) {

                    List<String> fields;
                    switch (SearchType.valueOf(searchType)) {
                        case TITLE -> fields = List.of("title");
                        case DESCRIPTION -> fields = List.of("description");
                        case HASHTAG -> fields = List.of("hashtag");
                        default -> fields = List.of("title", "description", "hashtag");
                    }
                    // 각 토큰마다 (nori OR ngram) 조건 추가
                    BoolQuery.Builder tokenBool = QueryBuilders.bool();
                    for (String field : fields) {
                        tokenBool.should(Query.of(q -> q.match(m -> m
                                .field(field)
                                .query(token)
                        )));
                    }

                    // ngram analyzer 필드별 OR 조건 추가
                    for (String field : fields) {
                        tokenBool.should(Query.of(q -> q.match(m -> m
                                        .field(field + ".ngram")
                                        .query(token)
                        )));
                    }
                    boolQuery.must(tokenBool.build()._toQuery()); // 모든 토큰 필수
                }
            }
        }

        // 3. 카테고리 필터링
        if (category != null && category != 0) {
            boolQuery.filter(Query.of(q -> q
                    .term(t -> t.field("category").value(category))
            ));
        }

        // 4. 게시판 필터링
        System.out.println(boardType.name());
        boolQuery.filter(Query.of(q -> q.term(t -> t
                .field("board")  // ES 문서에 정의된 필드명
                .value(boardType.name())  // enum 이름을 문자열로 비교
        )));


        // 5. 마감글/예정글 필터링
        String now = String.valueOf(Instant.now().toEpochMilli());
        if (!includeExpired) {
            boolQuery.filter(Query.of(q -> q.bool(b -> b
                    .should(s -> s.range(r -> r
                            .untyped(u -> u
                                    .field("end_time")
                                    .gte(JsonData.of(now))
                            )
                    ))
                    .should(s -> s.bool(bb -> bb.mustNot(m -> m.exists(e -> e.field("end_time")))))
            )));
        }
        if (!includeScheduled) {
            boolQuery.filter(Query.of(q -> q.bool(b -> b
                    .should(s -> s.range(r -> r
                            .untyped(u -> u
                                    .field("start_time")
                                    .lte(JsonData.of(now))
                            )
                    ))
                    .should(s -> s.bool(bb -> bb.mustNot(m -> m.exists(e -> e.field("start_time")))))
            )));
        }

        // 6. 정렬조건 추가
        Sort sort = switch (orderBy) {
            case "old" -> Sort.by(Sort.Direction.ASC, "pulled_at");
            case "close" -> Sort.by(Sort.Direction.ASC, "end_time");
            case "like" -> Sort.by(Sort.Direction.DESC, "likes");
            case "view" -> Sort.by(Sort.Direction.DESC, "views");
            default -> Sort.by(Sort.Direction.DESC, "pulled_at");
        };

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(Query.of(q -> q.bool(boolQuery.build())))
                .withSort(sort)
                .withPageable(PageRequest.of(page, pageSize))
                .build();
        SearchHits<SearchDocument> searchHits =  elasticsearchOperations.search(nativeQuery, SearchDocument.class);

        List<String> userIds = searchHits.getSearchHits().stream()
                .map(hit -> hit.getContent().getUserId())
                .distinct()
                .toList();

        Map<String, UserEntity> userMap = userRepository.findAllById(userIds)
                .stream().collect(Collectors.toMap(UserEntity::getId, Function.identity()));

        List<SearchDocWithUserResponse> results = searchHits.getSearchHits().stream()
                .map(hit -> {
                    SearchDocument doc = hit.getContent();
                    UserEntity user = userMap.get(doc.getUserId());
                    return searchConverter.toSearchPostWithUserResponse(doc, user);
                })
                .toList();

        return new PageImpl<>(results, PageRequest.of(page, pageSize), searchHits.getTotalHits());
    }

    private SearchDocument findSearchDocByIdAndBoardWithThrow(String id){
        return Optional.ofNullable(elasticsearchOperations.get(id, SearchDocument.class))
                .orElseThrow(() -> new EntityNotFoundException("Document not found"));
    }




    //인기 제품들 조회ㅇㅇ
    // 인기 제품들 조회
    public List<SearchDocument> getTopViewedDocuments(Board boardType, int size) {
        BoolQuery.Builder boolQuery = QueryBuilders.bool();
        boolQuery.filter(Query.of(q -> q.term(t -> t
                .field("board.keyword")  // ← 이렇게 해보자. board 필드의 keyword 타입 강제 호출
                .value(boardType.name())
        )));

        log.info("🔍인기 제품들 조회- [ES 쿼리조건] board: {}", boardType.name());

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(Query.of(q -> q.bool(boolQuery.build())))
                .withSort(Sort.by(Sort.Direction.DESC, "views"))
                .withPageable(PageRequest.of(0, size))
                .build();

        SearchHits<SearchDocument> hits = elasticsearchOperations.search(nativeQuery, SearchDocument.class);

        List<SearchDocument> docs = hits.getSearchHits().stream()
                .map(hit -> hit.getContent())
                .toList();

        log.info("📦인기 제품들 조회- [ES 조회결과] 총 {}건", docs.size());

        if (docs.isEmpty()) {
            log.warn("❗인기 제품들 조회- [ES 조회] 결과 없음. 색인이 없거나 board 필드 불일치일 수 있음.");
        }

        for (int i = 0; i < docs.size(); i++) {
            SearchDocument doc = docs.get(i);
            log.info("📄 [{}위] id={}, board={}, views={}, title={}",
                    (i + 1), doc.getId(), doc.getBoardType(), doc.getViews(), doc.getTitle());
        }

        return docs;
    }

}
