package com.goodsmoa.goodsmoa_BE.Qna.Entity;



import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_question")
public class UserQuestionEntity {

    // 문의 아이디 (✔ private Long id;를 사용하면 DB에서 자동으로 BIGINT로 변환됨)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ( FK) 회원 아이디 user_id.
    //나중에 DB만들때 user_id 필드 외래키로 생김
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    // 문의 제목
    @Column(name = "title", length = 30, nullable = false)
    private String title;

    // 질문 내용
    @Column(name = "req_content", length = 255, nullable = false)
    private String reqContent;

    // 답변 내용 (NULL 가능)
    @Column(name = "res_content", length = 255)
    private String resContent;

    // 문의 생성 날짜
    @Column(name = "req_created_at", nullable = false)
    private LocalDateTime reqCreatedAt;

    // 문의 수정 날짜( LocalDateTime은 초(Sec)까지 포함해서 나옴 ex) 2025-03-11T14:23:45.678 )
    @Column(name = "req_updated_at")
    private LocalDateTime reqUpdatedAt;


    //답변 생성 날짜
    @Column(name = "res_created_at")
    private LocalDateTime resCreatedAt;



    //답변 수정 날짜
    @Column(name = "res_updated_at")
    private LocalDateTime resupdatedAt;






    // 문의글 생성
    public void createQuestion(UserEntity user) {
        this.user = user;
        this.reqCreatedAt = LocalDateTime.now();
    }

    //문의글 수정할떄 실행되는 메서드
    public void updateQuestion(String title, String content) {
        if (title != null) {
            this.title = title;
        }
        if (content != null) {
            this.reqContent = content;
        }
        this.reqUpdatedAt = LocalDateTime.now();
    }


    //문의글에 답변달기 메서드
    public void createAnswer(String resContent) {
        this.resContent = resContent;
        this.resCreatedAt = LocalDateTime.now();
    }


    //문의글에 단 답변 수정하기 메서드
    public void updateAnswer(String resContent) {
        this.resContent = resContent;
        this.resupdatedAt = LocalDateTime.now();
    }

    public void deleteAnswer() {
        this.resContent = null;
        this.resupdatedAt = LocalDateTime.now();
    }



}
