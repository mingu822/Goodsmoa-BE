package com.goodsmoa.goodsmoa_BE.chat.Config;

import com.goodsmoa.goodsmoa_BE.security.provider.JwtProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@EnableWebSocketMessageBroker
@Configuration
@Slf4j
public class WebSocketStompConfig implements WebSocketMessageBrokerConfigurer {

    private JwtProvider jwtProvider;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .addInterceptors(new JwtAuthInterceptor(jwtProvider))
                .setAllowedOrigins("*");

        registry.addEndpoint("/ws-sockjs") // SockJS용
                .setAllowedOriginPatterns("*")
//                .addInterceptors(new JwtAuthInterceptor(jwtProvider))
                .withSockJS();
    }
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/sub");
        registry.setApplicationDestinationPrefixes("/pub");
    }
}
