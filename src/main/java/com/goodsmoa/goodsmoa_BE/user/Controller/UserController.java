package com.goodsmoa.goodsmoa_BE.user.Controller;

import com.goodsmoa.goodsmoa_BE.security.provider.JwtProvider;
import com.goodsmoa.goodsmoa_BE.user.Converter.UserInfoConverter;
import com.goodsmoa.goodsmoa_BE.user.DTO.UserInfoResponseDto;
import com.goodsmoa.goodsmoa_BE.user.DTO.UserInfoUpdateRequestDto;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserAccountEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserAddressEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import com.goodsmoa.goodsmoa_BE.user.Repository.UserAccountRepository;
import com.goodsmoa.goodsmoa_BE.user.Repository.UserAddressRepository;
import com.goodsmoa.goodsmoa_BE.user.Repository.UserRepository;
import com.goodsmoa.goodsmoa_BE.user.Service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequestMapping("/users")
@RestController // ✅ @Controller 대신 @RestController 사용 (JSON 응답)
@RequiredArgsConstructor // ✅ 생성자 주입 자동 적용 (Lombok)
public class UserController {

    private final JwtProvider jwtProvider; // ✅ JwtProvider를 주입받음
    private final UserRepository userRepository;
    private final UserService userService;
    private final UserAddressRepository userAddressRepository;
    private final UserAccountRepository userAccountRepository;


    //엑세스 토큰 재발급받는 api
    @PostMapping("/auth/refresh")
    public ResponseEntity<?> refreshAccessToken(
            @CookieValue(value = "refreshToken", required = false) String refreshToken) {

        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("리프레시 토큰이 없습니다. 😢");
        }

        try {
            String newAccessToken = userService.reissueAccessTokenFromRefresh(refreshToken);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Set-Cookie", "accessToken=" + newAccessToken +
                    "; HttpOnly; Path=/; Max-Age=1800; SameSite=Lax");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body("엑세스 토큰 재발급 완료 ");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("재발급 실패: " + e.getMessage());
        }
    }




    //로그아웃 (서비스 추가)

    @PostMapping("/auth/logout")
    public ResponseEntity<?> logout(   @AuthenticationPrincipal UserEntity user,HttpServletResponse response ) {


        userService.removeRefreshToken(user);

        ResponseCookie deleteAccessToken = ResponseCookie.from("accessToken", "")
                .path("/")
                .maxAge(0)
                .httpOnly(true)
                .sameSite("Lax")
                .build();

        ResponseCookie deleteRefreshToken = ResponseCookie.from("refreshToken", "")
                .path("/")
                .maxAge(0)
                .httpOnly(true)
                .sameSite("Lax")
                .build();

        // 응답에 쿠키 두 개 다 추가
        response.addHeader("Set-Cookie", deleteAccessToken.toString());
        response.addHeader("Set-Cookie", deleteRefreshToken.toString());

        return ResponseEntity.ok("로그아웃 완료");


    }


    @GetMapping("/info")
    public ResponseEntity<UserInfoResponseDto> userInfo(@AuthenticationPrincipal UserEntity user) {

        UserEntity userEntity = userService.getUserById(user.getId());

        List<UserAddressEntity> addresses = userAddressRepository.findAllByUser(userEntity);
        UserAccountEntity account =
                userAccountRepository.findByUser(userEntity); // 1:1 관계니까 하나만

        UserInfoResponseDto dto = UserInfoConverter.toDto(userEntity, addresses, account);
        return ResponseEntity.ok(dto);
    }


    //유저정보 수정
    //PUT은 기존 자원을 "전체 수정"할 때 쓰는 HTTP 메서드야
    @PutMapping("/info")
    public ResponseEntity<String> updateUserInfo(
            @AuthenticationPrincipal UserEntity user,
            @RequestBody UserInfoUpdateRequestDto dto
    ) {

        UserEntity userentity=userService.getUserById(user.getId());
        userService.updateUser(userentity, dto);
        return ResponseEntity.ok("유저 정보와 배송지 수정 완료 ");
    }








}
