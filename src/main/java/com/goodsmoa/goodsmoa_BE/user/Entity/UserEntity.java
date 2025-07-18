package com.goodsmoa.goodsmoa_BE.user.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user") // 이 엔티티가 user 테이블과 매핑됨을 명시
public class UserEntity {

    @Id // 기본 키(PK) 지정
    @Column(name = "id", nullable = false) // 컬럼 이름 설정 및 NULL 값 허용 X
    private String id; // 유저 ID (기본 키)


    @Column(name = "name", length = 10) // 최대 길이 10 설정
    private String name; // 유저 이름


    @Column(name = "email", length = 30)
    private String email; // 이메일 주소

    @Column(name = "phone_number", length = 15) // 최대 길이 15 설정
    private String phoneNumber; // 전화번호

    @Column(name = "nickname", length = 30, nullable = false) // NULL 허용 X
    private String nickname; // 닉네임

    @Column(name = "image", length = 254) // 최대 길이 254 설정
    private String image; // 프로필 이미지 URL

    @Column(name = "content", length = 100) // 최대 길이 100 설정
    private String content; // 유저 소개글

    @Column(name = "Identity", columnDefinition = "TINYINT(1)") // Boolean 타입의 작은 정수 컬럼
    private Boolean identity; // 본인 인증 여부 (기본값 없음, NULL 허용)

    @Column(name = "panelty") // 제재(패널티) 횟수 저장
    private Integer penalty; // 패널티 횟수

    @Column(name = "status", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 1") // 기본값 1
    private Boolean status = true; // 계정 활성화 상태 (true: 활성, false: 비활성)

    @Column(name = "report_count", nullable = false, columnDefinition = "INT DEFAULT 0") // 기본값 0
    private Integer reportCount = 0; // 신고당한 횟수

    @Column(name = "role", nullable = false) // 기본값 0
    private String role;

    @Column(name = "refresh_token")
    private String refreshToken;



    //유저 정보 수정 메서드(setter 역할)
    public void updateUserInfo(String name, String nickname, String email, String phoneNumber, String content, String image) {
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.content = content;
        this.image = image;
    }

}
