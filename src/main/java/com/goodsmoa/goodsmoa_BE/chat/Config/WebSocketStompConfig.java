package com.goodsmoa.goodsmoa_BE.chat.Config;

import com.goodsmoa.goodsmoa_BE.security.provider.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


@EnableWebSocketMessageBroker
@Configuration
@Slf4j
@RequiredArgsConstructor
public class WebSocketStompConfig implements WebSocketMessageBrokerConfigurer {

    private JwtProvider jwtProvider;

    @Autowired
    private JwtAuthInterceptor jwtAuthInterceptor;

    private final StompJwtChannelInterceptor stompJwtChannelInterceptor;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        registry.addEndpoint("/ws")
//                .addInterceptors(new JwtAuthInterceptor(jwtProvider))
//                .setAllowedOrigins("*");
//
//        registry.addEndpoint("/ws-sockjs") // SockJS용
//                .setAllowedOriginPatterns("*")
////                .addInterceptors(new JwtAuthInterceptor(jwtProvider))
//                .withSockJS();
        registry.addEndpoint("/ws")
                .addInterceptors(jwtAuthInterceptor)
                .setAllowedOriginPatterns("*");

        registry.addEndpoint("/ws-sockjs")
                .addInterceptors(jwtAuthInterceptor)
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/sub");
        registry.setApplicationDestinationPrefixes("/pub");
    }
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompJwtChannelInterceptor);
    }

}
