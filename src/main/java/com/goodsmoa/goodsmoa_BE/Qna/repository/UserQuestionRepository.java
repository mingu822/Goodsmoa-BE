package com.goodsmoa.goodsmoa_BE.Qna.repository;



import com.goodsmoa.goodsmoa_BE.Qna.Entity.UserQuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserQuestionRepository extends JpaRepository<UserQuestionEntity, Long> {

    // 특정 유저의 문의 목록 조회------------------------------------------------------
    /* ✅ 왜 UserQuestionRepository에서 findByUserId()를 관리하는 걸까?->조회하려는 엔티티 기준으로 리포지터리 등록하기 떄문!
    ✔ 이유: findByUserId()는 "문의(user_question)"를 기준으로 특정 유저의 문의를 찾는 것이기 때문이야!
    ✔   즉, 조회하려는 엔티티(UserQuestion)가 기준이므로 UserQuestionRepository에서 관리하는 게 맞아!
    --> 즉, 리포지토리는 "관리하는 엔티티"에 따라 나뉜다!
    * */

    /*
    UserQuestionEntity는 UserEntity user를 가지고 있어! (외래키 관계)

    findByUserId(String userId)는 user.id 필드를 기준으로 검색하는 거야!

    JPA는 객체 탐색도 가능해서 user.id 도 인식할 수 있어
    *
    * */
    List<UserQuestionEntity> findByUserId(String userId);



}
