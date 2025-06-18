package com.goodsmoa.goodsmoa_BE.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // ✅ productPost 이미지: 서버 실행 중 저장됨 (썸네일, 상품 이미지, 본문 이미지)
        registry.addResourceHandler("/productPost/**")
                .addResourceLocations("file:src/main/resources/static/productPost/")
                .setCacheControl(CacheControl.noStore()); // 캐시 없음

        // ✅ trade 이미지: 서버 실행 중 저장됨 (썸네일 등)
        registry.addResourceHandler("/trade/**")
                .addResourceLocations("file:src/main/resources/static/trade/")
                .setCacheControl(CacheControl.noStore()); // 캐시 없음

        registry.addResourceHandler("/demandPost/**")
                .addResourceLocations("file:src/main/resources/static/demandPost/")
                .setCacheControl(CacheControl.noStore()); // 캐시 없음
    }
}