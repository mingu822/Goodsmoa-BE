package com.goodsmoa.goodsmoa_BE.search.document;

import com.goodsmoa.goodsmoa_BE.enums.Board;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "search_document")
public class SearchDocument {

    @Id
    @Field(type = FieldType.Text, index = false)
    private String id;

    @Field(type = FieldType.Text, name = "user_id", index = false)
    private String userId;

    @Field(type = FieldType.Text, name = "thumbnail_url", index = false)
    private String thumbnailUrl;

    @Field(type = FieldType.Long, name = "views", index = false)
    private Long views;

    //검색할 대상
    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "nori_analyzer"),
            otherFields = {
                    @InnerField(suffix = "ngram", type = FieldType.Text, analyzer = "ngram_analyzer")
            }
    )
    private String title;

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "nori_analyzer"),
            otherFields = {
                    @InnerField(suffix = "ngram", type = FieldType.Text, analyzer = "ngram_analyzer")
            }
    )
    private String description;

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "nori_analyzer"),
            otherFields = {
                    @InnerField(suffix = "ngram", type = FieldType.Text, analyzer = "ngram_analyzer")
            }
    )
    private String hashtag;

    //필터링용
    @Field(type = FieldType.Keyword, name = "board")
    private Board boardType;
    @Field(type = FieldType.Integer, name = "category")
    private Integer categoryId;

    //정렬용
    @Field(type = FieldType.Date, name = "start_time", format = {},  pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS||epoch_millis")
    private LocalDateTime startTime;
    @Field(type = FieldType.Date, name = "end_time", format = {},  pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS||epoch_millis")
    private LocalDateTime endTime;
    @Field(type = FieldType.Date, name = "pulled_at", format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS||epoch_millis")
    private LocalDateTime pulledAt;
}
