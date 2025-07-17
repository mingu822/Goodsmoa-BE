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

import java.io.IOException;

@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    public JwtRequestFilter(AuthenticationManager authenticationManager, JwtProvider jwtProvider) {
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
    }

    // ✅ Authorization 헤더에서 Bearer 토큰 추출하는 메서드
    private String extractTokenFromHeader(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7); // "Bearer " 이후의 토큰만 추출
        }
        return null;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String jwt = extractTokenFromHeader(request);
        log.info("🪪 헤더에서 꺼낸 accessToken: {}", jwt);

        if (jwt != null && !jwt.isEmpty() && jwtProvider.validateToken(jwt)) {
            Authentication authentication = jwtProvider.getAuthenticationToken(jwt);
            if (authentication != null && authentication.isAuthenticated()) {
                log.info("✅ 유효한 JWT, SecurityContext 설정 완료");
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                log.info("❌ JWT는 있지만 인증 실패");
            }
        } else {
            log.info("❌ 유효하지 않은 JWT. SecurityContext 초기화");
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    // ❌ 필터 제외 경로가 있다면 여기에 추가 (선택)
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String uri = request.getRequestURI();
        return uri.equals("/auth/refresh") || uri.equals("/auth/login");
    }
}
