package com.goodsmoa.goodsmoa_BE.security.service;

import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * ✅ OAuth2User를 구현한 CustomOAuth2User 클래스
 * - 우리 서버의 User 엔티티를 Spring Security에서 사용할 수 있도록 감싸는 역할!
 * - Spring Security의 OAuth2 인증 과정에서 사용됨.
 */
@Getter
public class CustomOAuth2User implements OAuth2User {

    private final UserEntity user; // 우리 서버의 유저 정보 (User 엔티티)
    private final Map<String, Object> attributes; // OAuth2에서 받은 사용자 정보 (카카오에서 받은 정보)

    //    /**
//     * ✅ CustomOAuth2User 생성자
//     * - 우리 User 엔티티와 OAuth2에서 받은 사용자 정보를 매핑해줌.
//     */
    public CustomOAuth2User(UserEntity user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }
    //
//    /**
//     * ✅ OAuth2에서 받은 사용자 정보를 반환하는 메서드
//     * - Spring Security가 사용자 정보를 가져갈 때 사용됨.
//     */
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    //    /**
//     * ✅ 사용자의 권한(ROLE)을 반환하는 메서드
//     * - Spring Security에서 이 사용자가 어떤 권한을 가졌는지 확인할 때 사용됨.
//     * -  'ROLE_USER' 또는 'ROLE_ADMIN'을 반환하도록 설정.
//     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(user.getRole())); // 🔥 그대로 사용 가능!
    }
    //
//
//
//    /**
//     * ✅ 사용자의 고유한 식별자를 반환하는 메서드
//     * - Spring Security에서 OAuth2 사용자 정보를 가져갈 때 사용됨.
//     * - 여기서는 사용자의 ID를 문자열로 반환하도록 설정.
//     */
    @Override
    public String getName() {
        return user.getId().toString();
    }
}
