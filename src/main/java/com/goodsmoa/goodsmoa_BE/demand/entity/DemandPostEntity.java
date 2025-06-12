package com.goodsmoa.goodsmoa_BE.demand.entity;

import com.goodsmoa.goodsmoa_BE.category.Entity.Category;
import com.goodsmoa.goodsmoa_BE.search.entity.SearchEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity @Getter @Builder
@AllArgsConstructor
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "demand_post")
public class DemandPostEntity implements SearchEntity {
    /**
     * TODO: 게시물 숨기기(신고횟수에 따른 비활성화)추가
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
    @Setter
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String description;

    // 이미지 Url
    @Setter
    private String imageUrl;

    // 해시태그
    private String hashtag;

    // 상태 false:신고로 인한 비활성화(검색불가, 참여불가)
    // 상태 true :활성화(시작일시와 종료일시에 따른 상태 세분화)
    //  ㄴ대기 : 검색 가능, 참여 불가능
    //  ㄴ진행 : 검색 가능, 참여 가능
    //  ㄴ종료 : 검색 가능, 참여 불가능
    @Column(nullable = false)
    private boolean state;

    // 조회수
    @Column(nullable = false)
    private Long views;

    // category N:1 연결, 즉시 조회
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;

    // 시작일시
    @Column(nullable = false)
    private LocalDateTime startTime;

    // 종료일시
    @Column(nullable = false)
    private LocalDateTime endTime;

    // 생성일시
    @Column(nullable = false)
    private LocalDateTime createdAt;

    // user N:1 연결, 지연조회
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    // demand_product 1:N으로 연결
    @JsonIgnore
    @OneToMany(mappedBy = "demandPostEntity", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<DemandPostProductEntity> products = new ArrayList<>();


    // 글 수정(상품 목록 제외)
    public void updateDemandEntity(String title, String description,
                                   LocalDateTime startTime, LocalDateTime endTime,
                                   String imageUrl, String hashtag, Category category) {
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.imageUrl = imageUrl;
        this.hashtag = hashtag;
        this.category = category;
    }

    // 조회수 증가
    public void increaseViewCount(){
        this.views++;
    }
}
