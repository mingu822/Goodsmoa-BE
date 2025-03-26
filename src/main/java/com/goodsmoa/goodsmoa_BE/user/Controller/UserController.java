package com.goodsmoa.goodsmoa_BE.user.Controller;

import com.goodsmoa.goodsmoa_BE.security.provider.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/users")
@RestController // ✅ @Controller 대신 @RestController 사용 (JSON 응답)
@RequiredArgsConstructor // ✅ 생성자 주입 자동 적용 (Lombok)
public class UserController {

    private final JwtProvider jwtProvider; // ✅ JwtProvider를 주입받음




    //엑세스 토큰 재발급받는 api
    @PostMapping("/auth/refresh")
    public ResponseEntity<?> refreshAccessToken(@CookieValue(value = "refreshToken", required = false) String refreshToken) {

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

        // 4️⃣ 새 엑세스 토큰을 응답 헤더에 추가
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + newAccessToken);
        log.info("**엑세스 토큰 재발급 완료~!!: " + newAccessToken);

        return ResponseEntity.ok()
                .headers(headers)
                .body("엑세스 토큰 재발급 성공! 🎉");
    }

}
