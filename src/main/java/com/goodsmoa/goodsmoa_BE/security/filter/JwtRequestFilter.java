package com.goodsmoa.goodsmoa_BE.security.filter;

import com.goodsmoa.goodsmoa_BE.security.provider.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import com.goodsmoa.goodsmoa_BE.security.constrants.SecurityConstants;

import java.io.IOException;
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    public JwtRequestFilter(AuthenticationManager authenticationManager, JwtProvider jwtProvider) {
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
    }





    //  쿠키에서 accessToken 꺼내는 함수
    private String getTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }







    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {










        // 1. JWT 추출
        // 1. 쿠키에서 accessToken 꺼내기
        String jwt = getTokenFromCookie(request);
        log.info("🍪 쿠키에서 꺼낸 accessToken: {}", jwt);


        // 2. accessToken이 없으면 그냥 다음 필터로
        if (jwt == null || jwt.isEmpty()) {

            filterChain.doFilter(request, response);
            return;
        }


        // 2. 인증 시도 (jwt 해석해 인증 정보를 담은 객체 반환)
        // JWT를 이용해 인증 정보를 얻음
        Authentication authentication = jwtProvider.getAuthenticationToken(jwt);

        if (authentication != null && authentication.isAuthenticated()) {
            // JWT로 인증이 성공적으로 이루어졌다면, 인증 완료 로그 출력
            log.info("JWT 를 통한 인증 완료");
        }

        // 3. JWT 검증
        // JWT가 유효한지 확인 (만료되었거나 변조되었으면 false 반환)
        boolean result = jwtProvider.validateToken(jwt);

        if (result) {
            // 유효한 JWT 토큰이면 인증 완료
            log.info("유효한 JWT 토큰 입니다.");

            SecurityContextHolder.getContext().setAuthentication(authentication);

        }

        if (!result) {
            // 토큰이 유효하지 않거나 만료되었으면 인증 정보를 제거하고 로그아웃 처리
            log.info("JWT 토큰 만료 또는 변조됨. 인증을 제거하고 로그아웃 처리.(securitycontextholer에서 제거)");
            SecurityContextHolder.clearContext();
        }

        // 4. 다음 필터로 진행
        // JWT가 검증되었거나 인증이 완료되었으면, 요청을 필터 체인의 다음 필터로 넘김
        filterChain.doFilter(request, response);
    }
}


