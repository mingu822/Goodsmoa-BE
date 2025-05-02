package com.goodsmoa.goodsmoa_BE.elasticsearch.Document;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "trade_post")
public class TradePostDocument {

    @Id
    private Long id;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Text)
    private String hashtag;

    @Field(type = FieldType.Text)
    private String content;
}
