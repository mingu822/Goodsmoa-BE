package com.goodsmoa.goodsmoa_BE.Qna.controller;



import com.goodsmoa.goodsmoa_BE.Qna.Entity.UserQuestionEntity;
import com.goodsmoa.goodsmoa_BE.Qna.converter.QuestionCreateConverter;
import com.goodsmoa.goodsmoa_BE.Qna.converter.QuestionUpdateConverter;
import com.goodsmoa.goodsmoa_BE.Qna.dto.*;
import com.goodsmoa.goodsmoa_BE.Qna.repository.UserQuestionRepository;
import com.goodsmoa.goodsmoa_BE.Qna.service.UserQuestionService;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/qna")
@RequiredArgsConstructor
public class QnaController {

    private final UserQuestionService userQuestionService;

    private final UserQuestionRepository userQuestionRepository;

    /**
     * 🔥 특정 유저의 모든 문의 목록 조회
     * @param user 요청을 보낸 사용자 (SecurityContext에서 가져옴)
     * @return 해당 유저가 작성한 문의 리스트 반환
     */

    /*5분 프론트 헤더: 엑세스 토큰 401 401만료에러
    ->프론트에서 리프레스
    *
    * ✅ ResponseEntity란?
    👉 **"HTTP 응답을 만드는 객체"**야! 🚀
    👉 "상태 코드(200, 400, 500 등) + 데이터 + 헤더" 를 함께 보낼 수 있어!
    * */
    @GetMapping("/myQuestions")
    public ResponseEntity<List<UserQuestionEntity>> getUserQuestions(@AuthenticationPrincipal UserEntity user) {
        List<UserQuestionEntity> questions = userQuestionService.getUserQuestions(user.getId());
        // "200 OK + 데이터를 같이 보낸다" 라는 뜻
        return ResponseEntity.ok(questions);
    }









    /**
     * 🔥 새로운 문의 생성
     * - 로그인한 사용자가 새로운 문의를 등록하는 기능
     *
     * @param user 현재 로그인한 사용자 (SecurityContext에서 가져옴)
     */


    @PostMapping ("/question/create")
    public ResponseEntity<QuestionCreateResponseDto> createQuestion(
            @AuthenticationPrincipal UserEntity user,  // 🔥 여기가 null일 가능성 있음!
            @RequestBody QuestionCreateRequestDto request) {



        //요청 dto->엔티티변환
        UserQuestionEntity requestEntity= QuestionCreateConverter.toEntity(request);


        //서비스에서 처리하고 db저장
        UserQuestionEntity question = userQuestionService.createQuestion(user,requestEntity);

        //엔티티->응답 dto변환
        QuestionCreateResponseDto responsedto= QuestionCreateConverter.toDto(question);

        return ResponseEntity.ok(responsedto);
    }


     /**
     * 🔥 문의 수정 (문의 작성자만 가능)
     */

     @PutMapping("/question/update/{questionId}")
     public ResponseEntity<QustionUpdateResponseDto> updateQuestion(
             @PathVariable Long questionId,
             @AuthenticationPrincipal UserEntity user,
             @RequestBody QuestionUpdateRequestDto request) {  // ✅ JSON 요청에서 수정할 데이터 받음


         //요청dto->문의 엔티티(UserQuestionEntity requestEntity) 변환
         UserQuestionEntity requestEntity= QuestionUpdateConverter.toEntity(request);

         // ✅ 본인이 작성한 글인지 확인
         UserQuestionEntity question = userQuestionService.getQuestionById(questionId);
         if (!question.getUser().getId().equals(user.getId())) {
             return ResponseEntity.status(403).build(); // 🔥 403 Forbidden
         }

         // ✅ 서비스에서 수정 처리하고 db저장
         UserQuestionEntity updatedQuestion = userQuestionService.updateQuestion(questionId, requestEntity);

         // ✅ 엔티티 → 응답 DTO 변환
         QustionUpdateResponseDto responseDto = QuestionUpdateConverter.toDto(updatedQuestion);

         return ResponseEntity.ok(responseDto);
     }


    /**
     * 🔥 문의글 삭제 (문의 작성자만 가능)
     */
    @DeleteMapping("question/delete/{questionId}")
    public ResponseEntity<String> deleteQuestion (
            @PathVariable Long questionId,  // ✅ 삭제할 문의 ID (PathVariable로 받음)
            @AuthenticationPrincipal UserEntity user) {  // ✅ 로그인한 유저 정보 (JWT에서 자동 주입)

        // ✅ 서비스 호출 (삭제 로직 실행)
        userQuestionService.deleteQuestion(questionId, user.getId());

        // ✅ 응답 반환 (200 OK + 메시지)
        return ResponseEntity.ok("해당 문의글이 삭제되었습니다.");
    }


    /**
     * 🔥 문의 답변 추가 (관리자만 가능, JSON 요청)
     */
    @PutMapping("/answer/create/{questionId}")
    public ResponseEntity<AnswerCreateResponseDto> addAnswer(
            @PathVariable Long questionId,
            @RequestBody AnswerCreateRequestDto request) {

        // ✅ 서비스 호출 (답변 추가).
        AnswerCreateResponseDto response = userQuestionService.addAnswer(questionId, request);


        return ResponseEntity.ok(response);
    }


    /**
     * 🔥 문의 답변 변경(관리자만 가능, JSON 요청)
     */
    @PutMapping("/answer/update/{questionId}")
    public ResponseEntity<AnswerUpdateResponseDto> UpdateAnswer(
            @PathVariable Long questionId,
            @RequestBody AnswerUpdateRequestDto request) {

        // ✅ 서비스 호출 (답변 추가).
        AnswerUpdateResponseDto response = userQuestionService.UpdateAnswer(questionId, request);


        return ResponseEntity.ok(response);
    }

    /**
     * 🔥 문의 답변 삭제
     */
    @PutMapping("/answer/delete/{questionId}")
    public ResponseEntity<AnswerDeleteResponseDto>  DeleteAnswer(
            @PathVariable Long questionId) {

        // ✅ 서비스 호출 (답변 추가).
        AnswerDeleteResponseDto response = userQuestionService.DeleteAnswer(questionId);
        return ResponseEntity.ok(response);



    }







}
