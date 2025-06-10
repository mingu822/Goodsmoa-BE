package com.goodsmoa.goodsmoa_BE.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.goodsmoa.goodsmoa_BE.security.filter.JwtRequestFilter;
import com.goodsmoa.goodsmoa_BE.security.provider.JwtProvider;
import com.goodsmoa.goodsmoa_BE.security.service.CustomOAuth2UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig {

    // ✅ 카카오 서비스 객체 주입
    @Autowired
    private CustomOAuth2UserService customOAuth2UserService; // ✅ 객체(Bean)로 주입

    // 비밀번호 암호화 빈 등록
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private JwtProvider jwtProvider;

    // AuthenticationManager를 빈으로 등록
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http
                .getSharedObject(AuthenticationManagerBuilder.class);
        return authenticationManagerBuilder.build();
    }

    // CORS 설정을 위한 CorsConfigurationSource 빈
    // ✅ CORS 설정 (React 프론트엔드 요청 허용)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // ✅ 정확한 Origin 명시
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5177/"));

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // HTTP 보안 설정을 위한 메서드
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 기존 설정 유지
        http.formLogin(login -> login.disable());
        http.httpBasic(basic -> basic.disable());
        http.csrf(csrf -> csrf.disable());
        http.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // CORS 설정 적용
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        // 인증 요청 경로 명확히 설정
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/login/**",
                        "/oauth2/**",
                        "/public/**",
                        "/error",
                        "/users/info", // 사용자 정보 조회 경로 추가
                        "/product/post", // 상품 목록 조회는 허용
                        "/product/post-detail/**", // 상품 상세 조회는 허용
                        "/order/create"
                ).permitAll()
                .requestMatchers(
                        "/mypage/**",
                        //"/orders/**",
                        "/cart/**",
                        "/product/post-create",
                        "/product/post-update",
                        "/product/post-delete/**")
                .authenticated() // 로그인 필요한 경로
                .anyRequest().permitAll());

        // JWT 필터 한 번만 등록 (중복 제거)
        http.addFilterBefore(
                new JwtRequestFilter(authenticationManager(http), jwtProvider),
                UsernamePasswordAuthenticationFilter.class);

        // OAuth2 로그인 설정
        http.oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo
                        .userService(customOAuth2UserService))
                .successHandler((request, response, authentication) -> {
                    response.sendRedirect("http://localhost:5177/");
                }));

        return http.build();
    }
}