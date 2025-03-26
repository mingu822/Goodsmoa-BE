package com.goodsmoa.goodsmoa_BE.Qna.service;


import com.goodsmoa.goodsmoa_BE.Qna.Entity.UserQuestionEntity;
import com.goodsmoa.goodsmoa_BE.Qna.dto.*;
import com.goodsmoa.goodsmoa_BE.Qna.repository.UserQuestionRepository;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import com.goodsmoa.goodsmoa_BE.user.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserQuestionService {


    // ✅ UserQuestionRepository를 주입받음 (Spring Bean으로 등록된 리포지토리를 가져와서 사용하는 것)
    private final UserQuestionRepository userQuestionRepository;

    // ✅ UserRepository를 주입받음
    private final UserRepository userRepository;




    /**
     * 🔥 id(pk)로 userquestion객체 찾기
     * @param  id 조회할 문의글의 id(pk)
     * @return 해당 문의글
     */
    public UserQuestionEntity getQuestionById(Long id) {
        return userQuestionRepository.findById(id).orElse(null);
    }



    /**
     * 🔥 특정 유저의 모든 문의 목록 조회 메서드
     * @param userId 조회할 유저의 ID (String 타입)
     * @return 해당 유저가 작성한 문의 리스트 반환 (List<UserQuestion>)
     */
    public List<UserQuestionEntity> getUserQuestions(String userId) {
        // ✅ UserQuestionRepository에서 userId를 기준으로 해당 유저의 문의글을 조회하여 반환
        return userQuestionRepository.findByUserId(userId);
    }

    /**
     * 🔥 새로운 문의를 생성하는 메서드 (저장)
     */
    @Transactional
    public UserQuestionEntity createQuestion(UserEntity user, UserQuestionEntity requestEntity) {
        // ✅ 생성 시간 자동 설정
        requestEntity.setReqCreatedAt(LocalDateTime.now());
        //fk인 (user테이블의 pk인 )user_id 저장
        requestEntity.setUser(user);

        // ✅ DB에 저장 후 반환
        return userQuestionRepository.save(requestEntity);
    }



    /**
     * 🔥 기존 문의를 수정하는 메서드
     */
    @Transactional
    public UserQuestionEntity updateQuestion(Long questionId,  UserQuestionEntity requestEntity) {

        // ✅ 문의 ID로 기존 문의글 엔티티 조회
        UserQuestionEntity question = userQuestionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("해당 문의가 존재하지 않습니다."));

        // ✅ 기존 문의글 엔티티 제목, 내용 수정
        question.setTitle(requestEntity.getTitle());
        question.setReqContent(requestEntity.getReqContent());
        question.setReqUpdatedAt(LocalDateTime.now()); // ✅ 수정 시간 업데이트

        // ✅ DB 저장 후 반환
        return userQuestionRepository.save(question);
    }

    /**
     * 🔥 문의글 삭제 (본인 글만 가능 or 관리자 권한으로 삭제)
     */
    @Transactional
    public void deleteQuestion(Long questionId, String userid) {
        // ✅ 1. 문의글 엔티티 가져오기
        UserQuestionEntity question = userQuestionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("문의글을 찾을 수 없습니다."));

        // ✅ 2. 현재 요청보낸 사용자 UseEntity 가져오기
        UserEntity user = userRepository.findById(userid).orElseThrow(() -> new RuntimeException("해당 사용자가 없습니당"));

        // ✅ 3. 관리자(ROLE_ADMIN)인지 확인
        if ("ROLE_ADMIN".equals(user.getRole())) { // ✅ 이렇게 수정!
            userQuestionRepository.delete(question);
            return; // deleteQuestion 종료
        }

        // ✅ 4. 본인 글인지 확인
        if (!question.getUser().getId().equals(user.getId()) ){
            throw new RuntimeException("본인만 삭제할 수 있습니다.");
        }

        // ✅ 5. 삭제 수행
        userQuestionRepository.delete(question);
    }






    //---------------------------------관리자가 답변 생성,수정하는 서비스--------------------------



    /**
     * 🔥 문의글에 답변 달기 (관리자만 가능)
     */
    @Transactional
    public AnswerCreateResponseDto addAnswer(Long questionId, AnswerCreateRequestDto answerRequest) {
        // ✅ 1. 문의글 찾기
        UserQuestionEntity question = userQuestionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("문의글을 찾을 수 없습니다."));

        // ✅ 2. 기존 문의글의 `res_content` 업데이트
        question.setResContent(answerRequest.getResContent());
        question.setResCreatedAt(LocalDateTime.now());

        // ✅ 3. 변경된 문의글 저장
        userQuestionRepository.save(question);

        // ✅ 4. 응답 DTO 반환
        return AnswerCreateResponseDto.builder()
                .id(question.getId())
                .resContent(question.getResContent())
                .resCreatedAt(question.getResCreatedAt())
                .title(question.getTitle())
                .build();
    }

    /**
     * 🔥 문의글에 답변 수정(관리자만 가능)
     */
    @Transactional
    public AnswerUpdateResponseDto UpdateAnswer(Long questionId, AnswerUpdateRequestDto answerRequest) {
        // ✅ 1. 기존 문의글 찾기
        UserQuestionEntity question = userQuestionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("문의글을 찾을 수 없습니다."));

        // ✅ 2. 기존 문의글의 `res_content` 업데이트, 수정 시간 반영
        question.setResContent(answerRequest.getResContent());
        question.setResupdatedAt(LocalDateTime.now());

        // ✅ 3. 변경된 문의글 저장
        userQuestionRepository.save(question);

        // ✅ 4. 응답 DTO 반환
        return AnswerUpdateResponseDto.builder()
                .id(question.getId())
                .resContent(question.getResContent())
                .resCreatedAt(question.getResCreatedAt())
                .resupdatedAt(question.getResupdatedAt())
                .title(question.getTitle())
                .build();
    }

    /**
     * 🔥 문의글에 답변 삭제(관리자만 가능)
     */
    @Transactional
    public AnswerDeleteResponseDto DeleteAnswer(Long questionId) {
        // ✅ 1. 기존 문의글 찾기
        UserQuestionEntity question = userQuestionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("문의글을 찾을 수 없습니다."));

        // ✅ 2. 기존 문의글의 `res_content` null로 변경, 수정 시간 반영
        question.setResContent(null);
        question.setResupdatedAt(LocalDateTime.now());

        // ✅ 3. 변경된 문의글 저장
        userQuestionRepository.save(question);

        // ✅ 4. 응답 DTO 반환
        return AnswerDeleteResponseDto.builder()
                .id(question.getId())
                .resContent(question.getResContent())
                .resCreatedAt(question.getResCreatedAt())
                .resupdatedAt(question.getResupdatedAt())
                .title(question.getTitle())
                .reqContent(question.getReqContent())
                .build();
    }




}
