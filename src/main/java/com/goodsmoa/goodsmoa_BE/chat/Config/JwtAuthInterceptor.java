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
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthInterceptor implements HandshakeInterceptor {

    private final JwtProvider jwtProvider;

//    public JwtAuthInterceptor(JwtProvider jwtProvider) {
//        this.jwtProvider = jwtProvider;
//    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler handler,
                                   Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpServletRequest httpServletRequest = servletRequest.getServletRequest();

            // 1. 쿠키에서 accessToken 추출 (기존 방식)
            String token = getTokenFromCookie(httpServletRequest);

            // 2. 쿼리 파라미터에서 accessToken 추출 (추가)
            if (token == null) {
                token = httpServletRequest.getParameter("accessToken");
                log.info("Access Token from Query Param: {}", token);
            } else {
                log.info("Access Token from Cookie: {}", token);
            }

            // 3. 토큰 검증
            if (token != null && jwtProvider.validateToken(token)) {
                Authentication authentication = jwtProvider.getAuthenticationToken(token);
                attributes.put("auth", authentication);
                log.info("WebSocket 인증 성공");
                return true;
            } else {
                log.warn("WebSocket 인증 실패: 토큰이 없거나 유효하지 않음");
            }
        }
        return false;
    }


    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler handler,
                               Exception ex) {}

//    private String getTokenFromCookie(ServerHttpRequest request) {
//        if (request instanceof ServletServerHttpRequest servletRequest) {
//            if (servletRequest.getServletRequest().getCookies() != null) {
//                for (jakarta.servlet.http.Cookie cookie : servletRequest.getServletRequest().getCookies()) {
//                    if ("accessToken".equals(cookie.getName())) {
//                        return cookie.getValue();
//                    }
//                }
//            }
//        }
//        return request.getHeaders().getFirst("Authorization");
//    }
    private String getTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}

