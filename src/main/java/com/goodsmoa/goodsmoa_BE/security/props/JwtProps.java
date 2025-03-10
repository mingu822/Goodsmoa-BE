package com.goodsmoa.goodsmoa_BE.security.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component    // 이 클래스를 Spring Bean으로 등록하라는 어노테이션. 스프링 컨테이너에 객체가 자동으로 등록됨.
@ConfigurationProperties("aloha") // "aloha" 접두사로 시작하는 프로퍼티들을 이 클래스의 필드에 바인딩하겠다고 설정.
public class JwtProps {

    // application.properties 파일에서 정의된 'aloha.secret-key'와 매칭되는 값을 이 필드에 자동으로 주입
    // 주로 JWT 인증에서 사용할 시크릿 키 값을 저장할 필드
    // 예: aloha.secret-key=|+<T%0h;[G97|I$5Lr?h]}`8rUX.7;0gw@bF<R/|"-U0n:...
    private String secretKey; // `aloha.secret-key` 값을 여기에 자동으로 주입받음
}
