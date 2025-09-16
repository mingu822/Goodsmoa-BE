package com.goodsmoa.goodsmoa_BE.security.filter;

import com.goodsmoa.goodsmoa_BE.security.provider.JwtProvider;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import com.goodsmoa.goodsmoa_BE.user.Service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
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

    public JwtRequestFilter(AuthenticationManager authenticationManager,
                            JwtProvider jwtProvider,
                            UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
        this.userService = userService;
    }

    // ✅ 쿠키에서 토큰 추출
    private String getTokenFromCookie(HttpServletRequest request, String tokenName) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (tokenName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    // ✅ 헤더에서 토큰 추출
    private String getTokenFromHeader(HttpServletRequest request, String headerName) {
        String header = request.getHeader(headerName);
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    // ✅ 새로운 accessToken을 쿠키로 내려줌 (웹용)
    private void addAccessTokenToCookie(HttpServletResponse response, String accessToken) {
        Cookie cookie = new Cookie("accessToken", accessToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 30); // 30분 유효
        response.addCookie(cookie);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1️⃣ accessToken 우선 추출 (헤더 → 쿠키 순서)
        String jwt = getTokenFromHeader(request, "Authorization");
        if (jwt == null) {
            jwt = getTokenFromCookie(request, "accessToken");
            if (jwt != null) log.info("🍪 쿠키에서 꺼낸 accessToken: {}", jwt);
        } else {
            log.info("📌 헤더에서 꺼낸 accessToken: {}", jwt);
        }

        if (jwt != null && jwtProvider.validateToken(jwt)) {
            // ✅ accessToken 유효 → SecurityContext 설정
            Authentication authentication = jwtProvider.getAuthenticationToken(jwt);
            if (authentication != null && authentication.isAuthenticated()) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("✅ 유효한 JWT, SecurityContext 설정 완료");
            }
        } else {
            // 2️⃣ accessToken 없음/만료 → refreshToken 사용
            String refreshToken = getTokenFromHeader(request, "Refresh");
            if (refreshToken == null) {
                refreshToken = getTokenFromCookie(request, "refreshToken");
                if (refreshToken != null) log.info("🍪 쿠키에서 꺼낸 refreshToken: {}", refreshToken);
            } else {
                log.info("📌 헤더에서 꺼낸 refreshToken: {}", refreshToken);
            }

            if (refreshToken != null && jwtProvider.validateToken(refreshToken)) {
                String userId = jwtProvider.extractUserIdFromRefreshToken(refreshToken);
                UserEntity user = userService.getUserById(userId);

                if (user != null) {
                    String newAccessToken = jwtProvider.createAccessToken(user);

                    // 👉 웹이면 쿠키로, 앱이면 헤더로 내려줄 수 있음
                    addAccessTokenToCookie(response, newAccessToken);
                    response.setHeader("Authorization", "Bearer " + newAccessToken);

                    log.info("🔄 accessToken 자동 재발급 완료");

                    Authentication newAuth = jwtProvider.getAuthenticationToken(newAccessToken);
                    if (newAuth != null && newAuth.isAuthenticated()) {
                        SecurityContextHolder.getContext().setAuthentication(newAuth);
                    }
                }
            } else {
                log.info("❌ refreshToken 없음 또는 만료, 인증 불가");
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}
