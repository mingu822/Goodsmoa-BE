package com.goodsmoa.goodsmoa_BE.chat.Config;

import com.goodsmoa.goodsmoa_BE.security.provider.JwtProvider;
import com.nimbusds.oauth2.sdk.util.JWTClaimsSetUtils;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class StompJwtChannelInterceptor implements ChannelInterceptor {

    private final JwtProvider jwtProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        try {
            StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
            log.info("🟡 [STOMP] preSend 진입: command={}", accessor.getCommand());

            if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                log.info("🟢 CONNECT 프레임 수신");

                // 권장 방식: Authorization 헤더 사용
                String authHeader = accessor.getFirstNativeHeader("Authorization");
                log.info("🛡️ Authorization header: {}", authHeader);

                String token = null;
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    token = authHeader.substring(7);
                }

                log.info("🟡 [STOMP] 최종 추출한 토큰: {}", token);

                if (token != null && jwtProvider.validateToken(token)) {
                    Authentication authentication = jwtProvider.getAuthenticationTokenForStomp(token);
                    accessor.setUser(authentication); // Authentication의 principal이 userId(String)
                    log.info("setUser principal 값: {}", authentication.getPrincipal()); // userId(String)이어야 함
                    log.info("👤 인증 유저: {}", authentication.getName());
                    log.info("👤 setUser 이전: {}", accessor.getUser());
//                    accessor.setUser(authentication);
                    log.info("👤 setUser 이후: {}", accessor.getUser());
                    log.info("🟢 [STOMP] 인증 성공");
                    return message;
                } else {
                    log.warn("🔴 [STOMP] 인증 실패: 토큰이 없거나 유효하지 않음");
                    throw new IllegalArgumentException("WebSocket 인증 실패: 토큰이 없거나 유효하지 않음");
                }
            }

            return message;

        } catch (Exception e) {
            log.error("🟡 [STOMP] preSend 예외 발생", e);
            throw e;
        }
    }
}

