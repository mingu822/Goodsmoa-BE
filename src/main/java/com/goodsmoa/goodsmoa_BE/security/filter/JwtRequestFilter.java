package com.goodsmoa.goodsmoa_BE.security.filter;

import com.goodsmoa.goodsmoa_BE.security.provider.JwtProvider;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import com.goodsmoa.goodsmoa_BE.user.Service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final UserService userService;

    public JwtRequestFilter(AuthenticationManager authenticationManager, JwtProvider jwtProvider, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
        this.userService = userService;
    }

    // ✅ Authorization: Bearer {accessToken}
    private String extractAccessToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    // ✅ Refresh: {refreshToken}
    private String extractRefreshToken(HttpServletRequest request) {
        return request.getHeader("Refresh");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String accessToken = extractAccessToken(request);
        log.info("🪪 헤더에서 꺼낸 accessToken: {}", accessToken);

        if (accessToken != null && jwtProvider.validateToken(accessToken)) {
            Authentication authentication = jwtProvider.getAuthenticationToken(accessToken);
            if (authentication != null && authentication.isAuthenticated()) {
                log.info("✅ accessToken 유효. SecurityContext 설정 완료");
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } else {
            // 🔄 accessToken이 없거나 만료됨 → refreshToken 사용
            String refreshToken = extractRefreshToken(request);
            log.info("🔁 accessToken 만료. refreshToken 시도: {}", refreshToken);

            if (refreshToken != null && jwtProvider.validateToken(refreshToken)) {
                String userId = jwtProvider.extractUserIdFromRefreshToken(refreshToken);
                UserEntity user = userService.getUserById(userId);

                if (user != null) {
                    String newAccessToken = jwtProvider.createAccessToken(user);
                    log.info("✅ accessToken 자동 재발급 완료");

                    // 🔄 새 accessToken과 기존 refreshToken 헤더로 응답에 실어보냄
                    response.setHeader("Authorization", "Bearer " + newAccessToken);
                    response.setHeader("Refresh", refreshToken); // 그대로 유지 (재발급 아님)

                    Authentication newAuth = jwtProvider.getAuthenticationToken(newAccessToken);
                    if (newAuth != null && newAuth.isAuthenticated()) {
                        SecurityContextHolder.getContext().setAuthentication(newAuth);
                    }
                }
            } else {
                log.info("❌ refreshToken 없음 또는 만료. SecurityContext 초기화");
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}
