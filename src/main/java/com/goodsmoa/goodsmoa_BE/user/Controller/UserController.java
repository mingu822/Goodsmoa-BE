package com.goodsmoa.goodsmoa_BE.user.Controller;

import com.goodsmoa.goodsmoa_BE.security.provider.JwtProvider;
import com.goodsmoa.goodsmoa_BE.user.DTO.UserInfoResponseDto;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import com.goodsmoa.goodsmoa_BE.user.converter.UserInfoConverter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/users")
@RestController // ✅ @Controller 대신 @RestController 사용 (JSON 응답)
@RequiredArgsConstructor // ✅ 생성자 주입 자동 적용 (Lombok)
public class UserController {

    private final JwtProvider jwtProvider; // ✅ JwtProvider를 주입받음




    //엑세스 토큰 재발급받는 api
    @PostMapping("/auth/refresh")
    public ResponseEntity<?> refreshAccessToken(@CookieValue(value = "refreshToken", required = false) String refreshToken,  HttpServletResponse response) {


        // 1️⃣ 리프레시 토큰이 없을 때
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("리프레시 토큰이 없습니다. 😢");
        }

        log.info("**리프레시 토큰 확인함: " + refreshToken);

        // 2️⃣ 토큰이 유효한지 검증 (JwtProvider의 validateToken 사용)
        if (!jwtProvider.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 리프레시 토큰입니다. ⛔");
        }

        // 3️⃣ 리프레시 토큰에서 유저 정보 추출 및 DB 검증
        String newAccessToken = jwtProvider.refreshAccessToken(refreshToken);
        if (newAccessToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("리프레시 토큰 검증 실패! 🚫");
        }

        // 4️⃣ 새 엑세스 토큰을 쿠키로 보냄
        // ✅ accessToken도 HttpOnly 쿠키로 내려줌
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", newAccessToken)
                .httpOnly(true)
                .path("/")
                .maxAge(1800) // 30분
                .sameSite("Lax") // 개발용이니까 Lax (배포시 Secure 추가)
                .build();

        log.info("새로 발급받은 엑세스토큰:" ,newAccessToken);

        response.addHeader("Set-Cookie", accessCookie.toString());

        return ResponseEntity.ok("AccessToken을 쿠키에 담아 보냈습니다! 🎉");
    }



    @GetMapping("/info")
    public ResponseEntity<UserInfoResponseDto> getUserInfo(@AuthenticationPrincipal UserEntity user) {

        // DTO로 변환
        UserInfoResponseDto userinfodto = UserInfoConverter.toDto(user);

        return ResponseEntity.ok(userinfodto);
    }




    /*


    ✅ HttpServletResponse란?
    서버 → 클라이언트로 응답 보낼 때 쓰는 "응답 객체"야!
    왜 쓰나?	쿠키, 헤더, 상태코드 등 응답 세부 설정하려고(JSON 데이터를 보내거나, 쿠키를 심어주거나)
    HttpServletResponse를 파라미터로 선언해두면 👉 Spring이 자동으로 주입해줘!

    *
    * */

    @PostMapping("/auth/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {

        // ✅ accessToken 쿠키 삭제
        ResponseCookie deleteAccessToken = ResponseCookie.from("accessToken", "")
                .path("/")
                .maxAge(0)
                .httpOnly(true)
                .sameSite("Lax")
                .build();

        // ✅ refreshToken 쿠키 삭제
        ResponseCookie deleteRefreshToken = ResponseCookie.from("refreshToken", "")
                .path("/")
                .maxAge(0)
                .httpOnly(true)
                .sameSite("Lax")
                .build();

        // 응답에 쿠키 두 개 다 추가
        response.addHeader("Set-Cookie", deleteAccessToken.toString());
        response.addHeader("Set-Cookie", deleteRefreshToken.toString());

        return ResponseEntity.ok("로그아웃 완료! 👋");
    }




}
