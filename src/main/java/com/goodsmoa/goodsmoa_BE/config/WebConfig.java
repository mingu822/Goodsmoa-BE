package com.goodsmoa.goodsmoa_BE.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // productPost 하위의 모든 정적 이미지 경로에 대한 캐시 제어
        registry.addResourceHandler("/productPost/**")
                .addResourceLocations("file:src/main/resources/static/productPost/")
                .setCacheControl(CacheControl.noStore()); // 캐시 사용 안 함
    }
}