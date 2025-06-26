package com.goodsmoa.goodsmoa_BE.search.converter;

import com.goodsmoa.goodsmoa_BE.search.dto.SearchDocWithUserResponse;
import com.goodsmoa.goodsmoa_BE.search.document.SearchDocument;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SearchConverter {
    private final Map<Class<?>, DocumentConverter<?>> converters = new HashMap<>();

    @Autowired
    public SearchConverter(List<DocumentConverter<?>> converterList) {
        for (DocumentConverter<?> converter : converterList) {
            // 리플렉션으로 제네릭 타입 추출 (예: TradePostEntity)
            Class<?> entityType = getEntityType(converter);
            converters.put(entityType, converter);
        }
    }

    private Class<?> getEntityType(DocumentConverter<?> converter) {
        Type[] interfaces = converter.getClass().getGenericInterfaces();
        ParameterizedType type = (ParameterizedType) interfaces[0];
        return (Class<?>) type.getActualTypeArguments()[0];
    }

    public <T> SearchDocument toDocument(T entity){
        DocumentConverter<T> converter = (DocumentConverter<T>)converters.get(entity.getClass());
        if(converter == null){
            throw new IllegalArgumentException("변환기 없음"+entity.getClass().getName());
        }
        return converter.convert(entity);
    }

    public SearchDocWithUserResponse toSearchPostWithUserResponse(SearchDocument doc, UserEntity user){
        return SearchDocWithUserResponse.builder()
                .id(doc.getId())
                .boardType(doc.getBoardType().getName())
                .title(doc.getTitle())
                .hashtag(doc.getHashtag())
                .thumbnailUrl(doc.getThumbnailUrl())
                .views(doc.getViews())
                .likes(doc.getLikes())
                .endTime(doc.getEndTime())
                .nickname(user.getNickname())
                .profileUrl(user.getImage())
                .build();
    }
}
