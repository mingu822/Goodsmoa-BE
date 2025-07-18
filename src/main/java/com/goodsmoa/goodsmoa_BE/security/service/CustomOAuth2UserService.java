package com.goodsmoa.goodsmoa_BE.security.service;

import com.goodsmoa.goodsmoa_BE.security.constrants.SecurityConstants;
import com.goodsmoa.goodsmoa_BE.security.provider.JwtProvider;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import com.goodsmoa.goodsmoa_BE.user.Repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final HttpServletResponse response;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        log.info("{} 로그인 시작", registrationId);

        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String id = null, nickname = null, name = null, email = null, phoneNumber = null;

        if ("naver".equals(registrationId)) {
            Map<String, Object> res = (Map<String, Object>) attributes.get("response");
            id = res.get("id").toString();
            nickname = (String) res.get("nickname");
            name = (String) res.get("name");
            email = (String) res.get("email");
            phoneNumber = (String) res.get("mobile");
        } else if ("kakao".equals(registrationId)) {
            Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
            Map<String, Object> profile = (Map<String, Object>) account.get("profile");
            id = attributes.get("id").toString();
            nickname = (String) profile.get("nickname");
        } else if ("google".equals(registrationId)) {
            id = attributes.get("sub").toString();
            nickname = (String) attributes.get("name");
            email = (String) attributes.get("email");
        }

        Optional<UserEntity> optionalUser = userRepository.findById(id);
        UserEntity user;

        if (optionalUser.isPresent()) {
            user = optionalUser.get();
        } else {
            user = new UserEntity();
            user.setId(id);
            user.setNickname(nickname);
            user.setRole("ROLE_USER");
            if ("naver".equals(registrationId)) {
                user.setName(name);
                user.setEmail(email);
                user.setPhoneNumber(phoneNumber);
            } else if ("google".equals(registrationId)) {
                user.setEmail(email);
            }
            user = userRepository.save(user);
        }

        if ("naver".equals(registrationId)) {
            user.setName(name);
            user.setEmail(email);
            user.setPhoneNumber(phoneNumber);
            userRepository.save(user);
        }

        String role = user.getRole();
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);

        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, Collections.singletonList(authority));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtProvider.createAccessToken(user);
        String refreshToken;

        String redisKey = "RT:" + user.getId();
        String existingEncryptedRT = redisTemplate.opsForValue().get(redisKey);

        if (existingEncryptedRT != null) {
            try {
                String decrypted = jwtProvider.decrypt(existingEncryptedRT);
                if (jwtProvider.validateToken(decrypted)) {
                    refreshToken = decrypted;
                } else {
                    refreshToken = issueNewRefreshToken(user, redisKey);
                }
            } catch (Exception e) {
                refreshToken = issueNewRefreshToken(user, redisKey);
            }
        } else {
            refreshToken = issueNewRefreshToken(user, redisKey);
        }

        // ✅ 토큰을 HTTP 응답 헤더로 전달
        response.setHeader("Authorization", "Bearer " + accessToken); // accessToken
        response.setHeader("Refresh", refreshToken); // refreshToken (프론트는 SecureStorage에 저장)

        log.info("✅ {} 로그인 완료! AccessToken: {}, RefreshToken: {}", registrationId, accessToken, refreshToken);

        return new CustomOAuth2User(user, attributes);
    }

    private String issueNewRefreshToken(UserEntity user, String redisKey) {
        try {
            String newRefreshToken = jwtProvider.createRefreshToken(user);
            String encrypted = jwtProvider.encrypt(newRefreshToken);
            redisTemplate.opsForValue().set(redisKey, encrypted, 30, TimeUnit.DAYS);
            return newRefreshToken;
        } catch (Exception e) {
            log.error("❌ 리프레시 토큰 발급/암호화 실패", e);
            throw new RuntimeException("리프레시 토큰 발급 실패", e);
        }
    }
}
