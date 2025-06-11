package com.goodsmoa.goodsmoa_BE.security.provider;


import com.goodsmoa.goodsmoa_BE.security.constrants.SecurityConstants;
import com.goodsmoa.goodsmoa_BE.security.props.JwtProps;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import com.goodsmoa.goodsmoa_BE.user.Service.UserService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Principal;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {


    private final JwtProps jwtProps;


    public SecretKey getShaKey() {
        // JwtProps에서 시크릿 키를 가져온다.
        String secretKey = jwtProps.getSecretKey();


        // 바이트 배열로 변환하여 HMAC-SHA 알고리즘에서 사용할 수 있는 SecretKey 객체를 생성한다.
        byte[] signingKey = secretKey.getBytes();
        return Keys.hmacShaKeyFor(signingKey); // SecretKey 객체 반환
    }


    //엑세스토큰 300분 설정 (개발용)
    public String createAccessToken(UserEntity user) {
        int exp = 1000 * 60 * 300;

        SecretKey shaKey = getShaKey();


        // JWT 토큰을 생성한다.
        String accessjwt = Jwts.builder()
                // 서명 생성: HMAC-SHA512 알고리즘을 사용하여 서명을 생성
                .signWith(shaKey, Jwts.SIG.HS512)
                // JWT 헤더에 "typ" 값 설정, "jwt"는 토큰의 유형을 나타냄
                .header().add("typ", SecurityConstants.TOKEN_TYPE)
                .and()
                // 토큰 만료 시간 설정
                .expiration(new Date(System.currentTimeMillis() + exp))
                // 페이로드에 username, role을 포함시켜서 토큰에 사용자 정보를 담음
                //JWT에서 Long 타입을 그대로 claim()에 넣으면 문제가 될 수 있습니다
                //형변환 필요

                .claim("id", user.getId())
                .claim("role", user.getRole())
                .claim("nickname", user.getNickname())
                // 모든 설정이 끝나면 최종적으로 JWT 토큰을 생성하고 반환
                .compact();


        log.info("accesstoken생성:" + accessjwt);
        return accessjwt;
    }


    //리프레시-30일
    public String createRefreshToken(UserEntity user) {
        long exp = 1000L * 60 * 60 * 24 * 30;  // ✅ long으로 변경

        SecretKey shaKey = getShaKey();

        // JWT 토큰을 생성한다.
        String refreshjwt = Jwts.builder()
                // 서명 생성: HMAC-SHA512 알고리즘을 사용하여 서명을 생성
                .signWith(shaKey, Jwts.SIG.HS512)
                // JWT 헤더에 "typ" 값 설정, "jwt"는 토큰의 유형을 나타냄
                .header().add("typ", SecurityConstants.TOKEN_TYPE)
                .and()
                // 토큰 만료 시간 설정
                .expiration(new Date(System.currentTimeMillis() + exp))
                // 페이로드에 username 포함 (role 불필요)
                .claim("id", user.getId())
                // 모든 설정이 끝나면 최종적으로 JWT 토큰을 생성하고 반환
                .compact();


        return refreshjwt;


    }


    //jWT 토큰을 해석하여 사용자 인증 정보를 반환하는 메서드

    public UsernamePasswordAuthenticationToken getAuthenticationToken(String authorization) {

        // Authorization 헤더가 null 이거나 빈 값일 경우, 인증을 진행할 수 없으므로 null 반환
        if (authorization == null || authorization.length() == 0)
            return null;

        try {
            // JWT 토큰 추출: Authorization 헤더에서 "Bearer " 부분을 제거하고, 실제 JWT 토큰만 추출
            String jwt = authorization.replace("Bearer ", "");
            log.info("jwt:" + jwt);

            // JWT 파싱(해석) (서명 검증 및 페이로드 추출)
            Jws<Claims> parsedToken = Jwts.parser()
                    .setSigningKey(getShaKey()) // 시크릿키를 사용해 서명 검증
                    .build()
                    .parseClaimsJws(jwt);


            log.info("parsedToken:" + parsedToken);


            // 사용자 id
            String id = parsedToken.getBody().get("id").toString();
            // 회원 권한
            String role = (String) parsedToken.getBody().get("role");

            String nickname = (String) parsedToken.getBody().get("nickname");

            // 해당 유저의 정보 담기 위해 Users 객체 생성
            UserEntity user = new UserEntity();
            user.setId(id);
            user.setRole(role);
            user.setNickname(nickname);


            //  권한을 SimpleGrantedAuthority로 변환 (DB에서 ROLE_ 형식으로 저장 중이므로 그대로 사용!)
            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));


            return new UsernamePasswordAuthenticationToken(user, null, authorities);

        } catch (ExpiredJwtException exception) {
            log.warn("만료된 JWT 토큰을 파싱하려는 시도: {}", exception.getMessage());
        } catch (UnsupportedJwtException exception) {
            log.warn("지원되지 않는 JWT 토큰을 파싱하려는 시도: {}", exception.getMessage());
        } catch (MalformedJwtException exception) {
            log.warn("잘못된 형식의 JWT 토큰을 파싱하려는 시도: {}", exception.getMessage());
        } catch (IllegalArgumentException exception) {
            log.warn("빈 JWT 토큰을 파싱하려는 시도: {}", exception.getMessage());
        }

        // 예외가 발생하면 null을 반환하여 인증을 실패시킨다.
        return null;
    }

    public UsernamePasswordAuthenticationToken getAuthenticationTokenForStomp(String jwt) {
        Jws<Claims> parsedToken = Jwts.parser()
                .setSigningKey(getShaKey())
                .build()
                .parseClaimsJws(jwt);

        String id = parsedToken.getBody().get("id").toString(); // userId
        log.info("유저인포에 대한 아이디 {}",id);
        String role = (String) parsedToken.getBody().get("role");
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

        // 반드시 principal에 id(String)만!
        return new UsernamePasswordAuthenticationToken(id, null, authorities);
    }



    public boolean validateToken(String jwt) {

        try {
            Jws<Claims> claims = Jwts.parser().verifyWith(getShaKey()).build().parseSignedClaims(jwt);
            Date expiration = claims.getBody().getExpiration();
            log.info("만료기간:" + expiration.toString());
            boolean result = expiration.after(new Date()); //만료안됐으면 true임
            return result;

        } catch (ExpiredJwtException exception) {
            log.error("토큰 만료");
        } catch (JwtException e) {
            log.error("토큰 손상");

        } catch (NullPointerException e) {
            log.error("토큰 없음");


        } catch (Exception e) {

        }
        return false;


    }

    // 암호화
    public String encrypt(String raw) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec key = new SecretKeySpec("MySuperSecretKey".getBytes(), "AES");

        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encrypted = cipher.doFinal(raw.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    // 복호화
    public String decrypt(String encoded) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec key = new SecretKeySpec("MySuperSecretKey".getBytes(), "AES");

        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decoded = Base64.getDecoder().decode(encoded);
        return new String(cipher.doFinal(decoded));
    }

    //리프레시 토큰에서 유저id 추출
    public String extractUserIdFromRefreshToken(String refreshToken) {
        Claims body = Jwts.parser()
                .setSigningKey(getShaKey())
                .build()
                .parseClaimsJws(refreshToken)
                .getBody();
        return body.get("id").toString();
    }




}
