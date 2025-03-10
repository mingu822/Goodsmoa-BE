package com.goodsmoa.goodsmoa_BE.security.constrants;

public final class SecurityConstants {

    // JWT 토큰을 HTTP 헤더에서 식별하는 데 사용되는 헤더 이름
    public static final String TOKEN_HEADER = "Authorization";

    // JWT 토큰의 접두사. 일반적으로 "Bearer " 다음에 실제 토큰이 옵니다.
    public static final String TOKEN_PREFIX = "Bearer ";

    // JWT 토큰의 타입을 나타내는 상수
    public static final String TOKEN_TYPE = "JWT";

    //로그인 경로
    public static final String LOGIN_URL="/login";

    // 이 클래스를 final로 선언하여 상속을 방지하고, 상수만을 정의하도록 만듭니다.
}
