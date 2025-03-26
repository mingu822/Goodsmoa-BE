package com.goodsmoa.goodsmoa_BE.security.service;

import com.goodsmoa.goodsmoa_BE.security.constrants.SecurityConstants;
import com.goodsmoa.goodsmoa_BE.security.provider.JwtProvider;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import com.goodsmoa.goodsmoa_BE.user.Repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
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

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final HttpServletResponse response; // ✅ 헤더에 추가하기 위해 주입

    public CustomOAuth2UserService(UserRepository userRepository, JwtProvider jwtProvider, HttpServletResponse response) {
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
        this.response = response;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // OAuth 제공자 (naver, kakao)

        log.info("{} 로그인 시작", registrationId);

        // ✅ OAuth2User 정보 가져오기
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String id = null;
        String nickname = null;
        String name = null;
        String email = null;
        String phoneNumber = null;

        // ✅ 네이버 로그인일 경우
        if ("naver".equals(registrationId)) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            id = response.get("id").toString();
            nickname = (String) response.get("nickname");
            name = (String) response.get("name"); // 네이버에서 제공하는 이름
            email = (String) response.get("email"); // 네이버 이메일
            phoneNumber = (String) response.get("mobile"); // 네이버 전화번호
        }
        // ✅ 카카오 로그인일 경우 (이메일, 전화번호 없음)
        else if ("kakao".equals(registrationId)) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
            id = attributes.get("id").toString();
            nickname = (String) profile.get("nickname");
        }
        // ✅ 구글 로그인
        else if ("google".equals(registrationId)) {
            id = attributes.get("sub").toString(); // 구글 고유 ID는 sub!
            nickname = (String) attributes.get("name");
            email = (String) attributes.get("email");

        }

// ✅ DB에서 사용자 조회 (없으면 저장)
        Optional<UserEntity> optionalUser = userRepository.findById(id);
        UserEntity user;

        if (optionalUser.isPresent()) {
            user = optionalUser.get();
        } else {
            // 새로운 유저 생성 (람다 표현식이 아닌 일반 코드로 처리)
            UserEntity newUser = new UserEntity();
            newUser.setId(id);
            newUser.setNickname(nickname);
            newUser.setRole("ROLE_USER"); // 일반 사용자

            // 네이버 로그인일 경우에 추가 정보 저장
            if ("naver".equals(registrationId)) {
                newUser.setName(name);
                newUser.setEmail(email);
                newUser.setPhoneNumber(phoneNumber);

                // 구글 로그인일 경우에 추가 정보 저장
            } else if ("google".equals(registrationId)) {
                newUser.setEmail(email);
            }


            user = userRepository.save(newUser);
        }

// ✅ 기존 유저일 경우, 네이버 로그인이라면 정보 업데이트
        if ("naver".equals(registrationId)) {
            user.setName(name);
            user.setEmail(email);
            user.setPhoneNumber(phoneNumber);
            userRepository.save(user); // 업데이트된 정보 저장
        }


        // ✅ 권한 설정
        String role = user.getRole();  // "ADMIN" 또는 "USER"
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);

        // ✅ Authentication 설정 (Spring Security)
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, Collections.singletonList(authority));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // ✅ JWT 액세스 토큰 발급
        String accessToken = jwtProvider.createAccessToken(user);
        String refreshToken = user.getRefreshToken();

        // ✅ 리프레시 토큰 갱신 필요하면 생성
        if (refreshToken == null || !jwtProvider.validateToken(refreshToken)) {
            refreshToken = jwtProvider.createRefreshToken(user);
            user.setRefreshToken(refreshToken);
            userRepository.save(user);
            log.info("리프레시 토큰 없거나 만료돼서 새로 발급함 ㅇㅇ;");
        }

        // ✅ 쿠키로 전달

        //응답헤더에 setcookie로 프론트에 쿠키 심어줌
        response.setHeader("Set-Cookie",
                "refreshToken=" + refreshToken + "; " +
                        "HttpOnly; " +
                        "Path=/; " +
                        "Max-Age=1800; " + // 30분
                        "SameSite=Lax"); // ✅ Secure 제거(배포할떄만쓰자), SameSite도 Lax로 변경!


        //응답헤더에 setcookie로 프론트에 쿠키 심어줌
        response.setHeader("Set-Cookie",
                "accessToken=" + accessToken + "; " +
                        "HttpOnly; " +
                        "Path=/; " +
                        "Max-Age=2592000; " + // 30일
                        "SameSite=Lax"); // ✅ Secure 제거(배포할떄만쓰자), SameSite도 Lax로 변경!


        log.info("✅ {} 로그인 완료! AccessToken: {}, RefreshToken: {}", registrationId, accessToken, refreshToken);

        return new CustomOAuth2User(user, attributes);
    }

}
