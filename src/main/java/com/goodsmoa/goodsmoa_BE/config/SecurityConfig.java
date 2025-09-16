package com.goodsmoa.goodsmoa_BE.config;

import java.util.Arrays;
import java.util.Map;

import com.goodsmoa.goodsmoa_BE.security.service.CustomOAuth2User;
import com.goodsmoa.goodsmoa_BE.user.Service.UserService;
import jakarta.servlet.http.Cookie;
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

    @Autowired
    private UserService userService;

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
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5177"));

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
                        "/order/create",
                        "/payment/**"
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
                new JwtRequestFilter(authenticationManager(http), jwtProvider,userService),
                UsernamePasswordAuthenticationFilter.class);

        // OAuth2 로그인 설정
        http.oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo
                        .userService(customOAuth2UserService))
                .successHandler((request, response, authentication) -> {
                    CustomOAuth2User customUser = (CustomOAuth2User) authentication.getPrincipal();
                    String accessToken = customUser.getAccessToken();
                    String refreshToken = customUser.getRefreshToken();

                    // 플랫폼 판별
                    String platform = request.getParameter("platform");
                    if (platform == null) {
                        String userAgent = request.getHeader("User-Agent");
                        if (userAgent != null && userAgent.toLowerCase().contains("okhttp")) {
                            platform = "app";
                        } else {
                            platform = "web";
                        }
                    }

                    if ("app".equalsIgnoreCase(platform)) {
                        // ✅ 앱인 경우 → JSON 응답
                        response.setContentType("application/json;charset=UTF-8");
                        String json = String.format(
                                "{\"accessToken\":\"%s\", \"refreshToken\":\"%s\"}",
                                accessToken, refreshToken
                        );
                        response.getWriter().write(json);
                        response.getWriter().flush();
                    } else {
                        // ✅ 웹인 경우 → 쿠키 저장 후 리다이렉트
                        Cookie accessCookie = new Cookie("accessToken", accessToken);
                        accessCookie.setHttpOnly(true);
                        accessCookie.setPath("/");
                        accessCookie.setMaxAge(60 * 30); // 30분
                        response.addCookie(accessCookie);

                        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
                        refreshCookie.setHttpOnly(true);
                        refreshCookie.setPath("/");
                        refreshCookie.setMaxAge(60 * 60 * 24 * 30); // 30일
                        response.addCookie(refreshCookie);

                        response.sendRedirect("http://localhost:5177/");
                    }
                })
        );

        return http.build();
    }
}