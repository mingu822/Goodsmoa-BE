package com.goodsmoa.goodsmoa_BE.search.converter;

import com.goodsmoa.goodsmoa_BE.search.document.SearchDocument;

public interface DocumentConverter<T> {
    SearchDocument convert(T entity);
}