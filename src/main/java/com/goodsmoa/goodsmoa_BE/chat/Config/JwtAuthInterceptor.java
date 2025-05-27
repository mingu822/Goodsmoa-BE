package com.goodsmoa.goodsmoa_BE.chat.Config;

import com.goodsmoa.goodsmoa_BE.security.provider.JwtProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;
@Slf4j
@Component
public class JwtAuthInterceptor implements HandshakeInterceptor {

    private final JwtProvider jwtProvider;

    public JwtAuthInterceptor(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler handler, Map<String, Object> attributes) {
        try {
            String token = getTokenFromCookie(request);
            log.info("Jwt Token :" +token);
            if (token != null && jwtProvider.validateToken(token)) {
                Authentication authentication = jwtProvider.getAuthenticationToken(token);
                attributes.put("auth", authentication); // ✅ 인증된 사용자 정보 저장
                log.info("WebSocket 인증성공");
                return true;
            }
        } catch (Exception e) {
            // 🔥 인증 실패 시, WebSocket 핸드셰이크 자체를 막지 않고 속성을 추가
            log.info("WebSocket 인승실패");
            attributes.put("auth", "unauthenticated");
        }
        return true; // ✅ 항상 WebSocket 연결을 허용하되, 인증 여부는 속성으로 저장
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler handler, Exception ex) {}

    private String getTokenFromCookie(ServerHttpRequest request) {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            if (servletRequest.getServletRequest().getCookies() != null) {
                for (jakarta.servlet.http.Cookie cookie : servletRequest.getServletRequest().getCookies()) {
                    if ("accessToken".equals(cookie.getName())) {
                        return cookie.getValue();
                    }
                }
            }
        }
        return request.getHeaders().getFirst("Authorization");
    }
}

