package com.goodsmoa.goodsmoa_BE.user.Controller;

import com.goodsmoa.goodsmoa_BE.security.provider.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController // ✅ @Controller 대신 @RestController 사용 (JSON 응답)
@RequiredArgsConstructor // ✅ 생성자 주입 자동 적용 (Lombok)
public class UserController {

    private final JwtProvider jwtProvider; // ✅ JwtProvider를 주입받음

    //엑세스 토큰 재발급받는 api
    @PostMapping("/auth/refresh")
    public ResponseEntity<?> refreshAccessToken(@RequestHeader("Refresh-Token") String refreshToken) {
        log.info("**엑세스 토큰 재발급받는 로직 실행~");
        //리프레시토큰 만료체크는 프론트에서하고 보낸거 ㅇㅇ
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("리프레시 토큰이 없습니다. 😢");
        }

        log.info("**리프레시 토큰 확인함:" + refreshToken);

        String newAccessToken = jwtProvider.refreshAccessToken(refreshToken);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + newAccessToken);
        log.info("**엑세스 토큰 재발급완료~:" + newAccessToken);

        return ResponseEntity.ok()
                .headers(headers)
                .body("엑세스 토큰 재발급 성공! 🎉");
    }

}
