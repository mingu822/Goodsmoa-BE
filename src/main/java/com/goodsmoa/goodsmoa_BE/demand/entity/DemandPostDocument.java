package com.goodsmoa.goodsmoa_BE.demand.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
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
@Document(indexName = "demand_post_entity")
public class DemandPostDocument {
    @Id
    private Long id;

    //검색할 대상
//    @Field(type = FieldType.Text, analyzer = "nori")
    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "nori_analyzer"),
            otherFields = {
                    @InnerField(suffix = "ngram", type = FieldType.Text, analyzer = "ngram_analyzer")
            }
    )
    private String title;
//    @Field(type = FieldType.Text, analyzer = "nori")
    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "nori_analyzer"),
            otherFields = {
                    @InnerField(suffix = "ngram", type = FieldType.Text, analyzer = "ngram_analyzer")
            }
    )
    private String description;
//    @Field(type = FieldType.Text, analyzer = "nori")
    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "nori_analyzer"),
            otherFields = {
                    @InnerField(suffix = "ngram", type = FieldType.Text, analyzer = "ngram_analyzer")
            }
    )
    private String hashtag;
//    @Field(type = FieldType.Text, name = "nick_name", analyzer = "nori")
    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "nori_analyzer"),
            otherFields = {
                    @InnerField(suffix = "ngram", type = FieldType.Text, analyzer = "ngram_analyzer")
            }
    )
    private String nickname;

    //필터링용
    @Field(type = FieldType.Integer, name = "category_id")
    private Integer categoryId;
    @Field(type = FieldType.Boolean, name = "is_safe_payment")
    private Boolean isSafePayment;
    @Field(type = FieldType.Boolean, name = "is_state")
    private Boolean isState;
    
    //정렬용
    @Field(type = FieldType.Date, name = "start_time", format = {},  pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS||epoch_millis")
    private LocalDateTime startTime;
    @Field(type = FieldType.Date, name = "end_time", format = {},  pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS||epoch_millis")
    private LocalDateTime endTime;
    @Field(type = FieldType.Date, name = "pulled_at", format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS||epoch_millis")
    private LocalDateTime pulledAt;
}
