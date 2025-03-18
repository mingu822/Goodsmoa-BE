package com.goodsmoa.goodsmoa_BE.demand.Entity;

import com.goodsmoa.goodsmoa_BE.user.Entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity @Getter @Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "demand")
public class DemandEntity {
    /**
     * TODO: 카테고리 Foreign Key 추가 필요함, 게시물 숨기기(신고횟수에 따른 비활성화)추가
     * FIXME: 자료형 확인, Valid 추가
    */
    // 수요조사글 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 제목
    @Column(nullable = false)
    private String title;

    // 내용,설명
    @Column(nullable = false)
    private String description;

    // 시작일시
    @Column(nullable = false)
    private LocalDateTime startTime;

    // 종료일시
    @Column(nullable = false)
    private LocalDateTime endTime;

    // 이미지 Url
    private String image;

    // 해시태그
    private String hashtag;

    // 상태 0:비공개(참여불가, 검색불가, *수정불가*)
    // 상태 1:공개(시작일시와 종료일시에 따른 상태 세분화)
    //  ㄴ대기 : 검색 가능, 참여 불가능
    //  ㄴ진행 : 검색 가능, 참여 가능
    //  ㄴ종료 : 검색 가능, 참여 불가능
    @Column(nullable = false)
    private boolean state;

    // 조회수
    @Column(nullable = false)
    private Long views = 0L;

    // 생성일시
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // 끌어올림일시
    @Column(nullable = false)
    private LocalDateTime pulledAt = LocalDateTime.now();

    // user N:1로 연결
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    
    // demand_product 1:N으로 연결
    // 교체시 통째로 교체함. 한명이라도 수요조사 참여시 수정 불가
    @OneToMany(mappedBy = "demandEntity", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<DemandProductEntity> products = new ArrayList<>();

    // 글 수정(상품 목록 제외)
    public void updateDemandEntity(String title, String description,
                                   LocalDateTime startTime, LocalDateTime endTime,
                                   String image, String hashtag) {
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.image = image;
        this.hashtag = hashtag;
    }
    
    // 끌어올림
    public void pull() {
        this.pulledAt = LocalDateTime.now();
    }
}
