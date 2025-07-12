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

    public JwtRequestFilter(AuthenticationManager authenticationManager, JwtProvider jwtProvider, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
        this.userService = userService;
    }

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

    private void addAccessTokenToCookie(HttpServletResponse response, String accessToken) {
        Cookie cookie = new Cookie("accessToken", accessToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 30); // 30분 유효
        response.addCookie(cookie);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String jwt = getTokenFromCookie(request, "accessToken");
        log.info("🍪 쿠키에서 꺼낸 accessToken: {}", jwt);

        if (jwt != null && !jwt.isEmpty() && jwtProvider.validateToken(jwt)) {
            Authentication authentication = jwtProvider.getAuthenticationToken(jwt);
            if (authentication != null && authentication.isAuthenticated()) {
                log.info("✅ 유효한 JWT, SecurityContext 설정 완료");
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } else {
            // 🚩 accessToken이 없거나 만료된 경우 refreshToken 사용하여 자동 재발급
            String refreshToken = getTokenFromCookie(request, "refreshToken");
            if (refreshToken != null && jwtProvider.validateToken(refreshToken)) {
                String userId = jwtProvider.extractUserIdFromRefreshToken(refreshToken);
                UserEntity user = userService.getUserById(userId);

                if (user != null) {
                    String newAccessToken = jwtProvider.createAccessToken(user);
                    addAccessTokenToCookie(response, newAccessToken);
                    log.info("✅ accessToken 자동 재발급 및 쿠키 갱신 완료");

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
