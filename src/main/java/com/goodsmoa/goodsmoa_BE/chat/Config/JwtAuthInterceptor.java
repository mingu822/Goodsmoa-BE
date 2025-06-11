package com.goodsmoa.goodsmoa_BE.chat.Config;

import com.goodsmoa.goodsmoa_BE.security.provider.JwtProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthInterceptor implements HandshakeInterceptor {

    private final JwtProvider jwtProvider;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler handler, Map<String, Object> attributes) throws Exception {
        log.info("🛡️ [Handshake] beforeHandshake 진입: {}", request.getURI());
        try {
            if (request instanceof ServletServerHttpRequest servletRequest) {
                HttpServletRequest httpServletRequest = servletRequest.getServletRequest();
                String token = getTokenFromCookie(httpServletRequest);
                log.info("🛡️ [Handshake] 쿠키에서 추출한 토큰: {}", token);
                System.out.println("🚨 JWT Interceptor 진입");
                System.out.println("accessToken: " + token);
                if (token == null) {
                    token = httpServletRequest.getParameter("accessToken");
                    log.info("🛡️ [Handshake] 쿼리파라미터에서 추출한 토큰: {}", token);
                }
                if (token != null && jwtProvider.validateToken(token)) {
                    Authentication authentication = jwtProvider.getAuthenticationToken(token);
//                    attributes.put("auth", authentication);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.info("🛡️ [Handshake] 인증 성공");
                    return true;
                } else {
                    log.warn("🛡️ [Handshake] 인증 실패: 토큰이 없거나 유효하지 않음");
                }
            }
        } catch (Exception e) {
            log.error("🛡️ [Handshake] 예외 발생", e);
        }
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler handler,
                               Exception ex) {}

    private String getTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}

