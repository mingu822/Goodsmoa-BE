package com.goodsmoa.goodsmoa_BE.chat.Config;

import com.goodsmoa.goodsmoa_BE.security.provider.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class StompJwtChannelInterceptor implements ChannelInterceptor {

    private final JwtProvider jwtProvider;

//    @Override
//    public Message<?> preSend(Message<?> message, MessageChannel channel) {
//        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
//
//        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
//            String cookieHeader = accessor.getFirstNativeHeader("cookie"); // 소문자 'cookie' 중요!
//
//            if (cookieHeader != null) {
//                String jwt = extractAccessTokenFromCookie(cookieHeader);
//
//                if (jwt != null) {
//                    UsernamePasswordAuthenticationToken authentication = jwtProvider.getAuthenticationToken(jwt);
//
//                    if (authentication != null && authentication.isAuthenticated()) {
//                        accessor.setUser(authentication);
//                    } else {
//                        log.warn("JWT 인증 실패: 인증 객체가 null이거나 인증되지 않음");
//                    }
//                } else {
//                    log.warn("accessToken이 쿠키에 존재하지 않음");
//                }
//            } else {
//                log.warn("쿠키 헤더가 존재하지 않음");
//            }
//        }
//
//        return message;
//    }//
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel)  {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = accessor.getFirstNativeHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            if (token != null && jwtProvider.validateToken(token)) {
                Authentication authentication = jwtProvider.getAuthenticationToken(token);
                accessor.setUser(authentication);
                log.info("STOMP CONNECT 인증 성공");
            } else {
                log.warn("STOMP CONNECT 인증 실패: 토큰이 없거나 유효하지 않음");
            }
        }
        return message;
    }


    private String extractAccessTokenFromCookie(String cookieHeader) {
        // "accessToken=ey..." 찾기
        String[] cookies = cookieHeader.split(";");
        for (String cookie : cookies) {
            cookie = cookie.trim();
            if (cookie.startsWith("accessToken=")) {
                return cookie.substring("accessToken=".length());
            }
        }
        return null;
    }

}

