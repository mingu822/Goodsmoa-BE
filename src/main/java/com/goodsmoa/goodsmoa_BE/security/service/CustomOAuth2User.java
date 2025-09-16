package com.goodsmoa.goodsmoa_BE.security.service;

import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Getter
public class CustomOAuth2User implements OAuth2User {

    private final UserEntity user; // 우리 서버 DB 유저
    private final Map<String, Object> attributes; // 카카오 등 OAuth2 provider 정보

    private final String accessToken;   // 🔥 새로 추가
    private final String refreshToken;  // 🔥 새로 추가

    public CustomOAuth2User(UserEntity user,
                            Map<String, Object> attributes,
                            String accessToken,
                            String refreshToken) {
        this.user = user;
        this.attributes = attributes;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(user.getRole()));
    }

    @Override
    public String getName() {
        return user.getId().toString();
    }
}
