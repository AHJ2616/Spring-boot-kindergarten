package com.kinder.kindergarten.service.board;

import com.github.f4b6a3.ulid.UlidCreator;
import com.kinder.kindergarten.DTO.survey.AnswerDTO;
import com.kinder.kindergarten.DTO.survey.QuestionDTO;
import com.kinder.kindergarten.DTO.survey.SurveyDTO;
import com.kinder.kindergarten.constant.board.QuestionType;
import com.kinder.kindergarten.entity.survey.AnswerEntity;
import com.kinder.kindergarten.entity.survey.QuestionEntity;
import com.kinder.kindergarten.entity.survey.SurveyEntity;
import com.kinder.kindergarten.repository.survey.AnswerRepository;
import com.kinder.kindergarten.repository.survey.QuestionRepository;
import com.kinder.kindergarten.repository.survey.SurveyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class SurveyService {

  private final SurveyRepository surveyRepository;
  private final QuestionRepository questionRepository;
  private final AnswerRepository answerRepository;
  private final ModelMapper modelMapper;

  /**
   * 설문조사를 생성하는 메소드
   * @param surveyDTO 설문조사 데이터 전송 객체
   * @return 생성된 설문조사 데이터 전송 객체
   */
  public SurveyDTO createSurvey(SurveyDTO surveyDTO) {
    try {
      SurveyEntity surveyEntity = new SurveyEntity();
      surveyEntity.setId(UlidCreator.getUlid().toString());
      surveyEntity.setTitle(surveyDTO.getTitle());
      surveyEntity.setDescription(surveyDTO.getDescription());
      surveyEntity.setWriter(surveyDTO.getWriter());

      surveyDTO.getQuestions().forEach(questionDTO -> {
        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setId();
        questionEntity.setText(questionDTO.getText());
        questionEntity.setType(questionDTO.getType());
        questionEntity.setOrderNumber(questionDTO.getOrderNumber());

        if ((questionDTO.getType() == QuestionType.SINGLE_CHOICE || 
             questionDTO.getType() == QuestionType.MULTIPLE_CHOICE) && 
            questionDTO.getOptions() != null && !questionDTO.getOptions().isEmpty()) {
          String optionsJson = String.join("|", questionDTO.getOptions());
          questionEntity.setOptions(optionsJson);
        }

        surveyEntity.addQuestion(questionEntity);
      });

      SurveyEntity savedEntity = surveyRepository.save(surveyEntity);
      return modelMapper.map(savedEntity, SurveyDTO.class);

    } catch (Exception e) {
      log.error("설문조사 생성 중 오류 발생: ", e);
      throw new RuntimeException("설문조사 생성 중 오류가 발생했습니다", e);
    }
  }

  /**
   * 설문조사를 조회하는 메소드
   * @param surveyId 설문 ID
   * @return 조회된 설문조사 데이터 전송 객체
   */
  @Transactional(readOnly = true)
  public SurveyDTO getSurvey(String surveyId) {
    SurveyEntity survey = surveyRepository.findById(surveyId)
            .orElseThrow(() -> new RuntimeException("설문조사를 찾을 수 없습니다."));
    return modelMapper.map(survey, SurveyDTO.class);
  }

  /**
   * 설문에 포함된 질문들을 조회하는 메소드
   * @param surveyId 설문 ID
   * @return 질문 데이터 전송 객체 리스트
   */
  @Transactional(readOnly = true)
  public List<QuestionDTO> getQuestions(String surveyId) {
    List<QuestionEntity> questions = questionRepository.findBySurveyId(surveyId);
    return questions.stream()
            .map(question -> {
              QuestionDTO questionDTO = modelMapper.map(question, QuestionDTO.class);
              if ((question.getType() == QuestionType.SINGLE_CHOICE || 
                   question.getType() == QuestionType.MULTIPLE_CHOICE) && 
                  question.getOptions() != null && !question.getOptions().isEmpty()) {
                questionDTO.setOptions(question.getOptionsList());
              }
              List<AnswerDTO> answerDTOs = question.getAnswers().stream()
                      .map(answer -> modelMapper.map(answer, AnswerDTO.class))
                      .collect(Collectors.toList());
              questionDTO.setAnswers(answerDTOs);
              return questionDTO;
            })
            .collect(Collectors.toList());
  }

  /**
   * 페이징된 설문조사 목록을 조회하는 메소드
   * @param pageable 페이징 정보
   * @return 페이징된 설문조사 데이터 전송 객체 페이지
   */
  @Transactional(readOnly = true)
  public Page<SurveyDTO> getAllSurveys(Pageable pageable) {
    Page<SurveyEntity> surveyPage = surveyRepository.findAll(pageable);
    return surveyPage.map(survey -> modelMapper.map(survey, SurveyDTO.class));
  }

  /**
   * 설문조사를 수정하는 메소드
   * @param surveyId 설문 ID
   * @param surveyDTO 수정할 설문조사 데이터 전송 객체
   * @return 수정된 설문조사 데이터 전송 객체
   */
  public SurveyDTO updateSurvey(String surveyId, SurveyDTO surveyDTO) {
    try {
      SurveyEntity survey = surveyRepository.findById(surveyId)
              .orElseThrow(() -> new RuntimeException("설문조사를 찾을 수 없습니다."));

      survey.setTitle(surveyDTO.getTitle());
      survey.setDescription(surveyDTO.getDescription());
      // 질문과 답변 업데이트 로직 추가...

      SurveyEntity updatedSurvey = surveyRepository.save(survey);
      return modelMapper.map(updatedSurvey, SurveyDTO.class);
    } catch (Exception e) {
      log.error("설문조사 수정 중 오류 발생: ", e);
      throw new RuntimeException("설문조사 수정 중 오류가 발생했습니다.", e);
    }
  }

  /**
   * 설문조사를 삭제하는 메소드
   * @param surveyId 설문 ID
   */
  public void deleteSurvey(String surveyId) {
    try {
      SurveyEntity survey = surveyRepository.findById(surveyId)
              .orElseThrow(() -> new RuntimeException("설문조사를 찾을 수 없습니다."));

      surveyRepository.delete(survey);
      log.info("설문조사 삭제 완료: {}", surveyId);
    } catch (Exception e) {
      log.error("설문조사 삭제 중 오류 발생: ", e);
      throw new RuntimeException("설문조사 삭제 중 오류가 발생했습니다.", e);
    }
  }

  /**
   * 설문 응답을 제출하는 메소드
   * @param surveyId 설문 ID
   * @param answerDTOList 응답 데이터 리스트
   */
  @Transactional
  public void submitSurveyResponse(String surveyId, List<Map<String, Object>> answers, String respondentId) {
    SurveyEntity survey = surveyRepository.findById(surveyId)
            .orElseThrow(() -> new RuntimeException("설문을 찾을 수 없습니다."));

    answers.forEach(answer -> {
        String questionId = (String) answer.get("questionId");
        String answerText = (String) answer.get("text");

        QuestionEntity question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("질문을 찾을 수 없습니다."));

        // 기존 답변이 있는지 확인
        Optional<AnswerEntity> existingAnswer = answerRepository
                .findByQuestionIdAndRespondentId(questionId, respondentId);

        if (existingAnswer.isPresent()) {
            // 기존 답변이 있으면 업데이트
            AnswerEntity answerEntity = existingAnswer.get();
            answerEntity.setText(answerText);
            answerRepository.save(answerEntity);
        } else {
            // 새로운 답변 생성
            AnswerEntity answerEntity = new AnswerEntity();
            answerEntity.setId(UlidCreator.getUlid().toString());
            answerEntity.setText(answerText);
            answerEntity.setQuestion(question);
            answerEntity.setRespondentId(respondentId);
            answerRepository.save(answerEntity);
        }
    });
  }

  /**
   * 설문에 대한 답변 목록을 조회하는 메소드
   * @param surveyId 설문 ID
   * @return 답변 데이터 전송 객체 리스트
   */
  @Transactional(readOnly = true)
  public List<AnswerDTO> getAnswers(String surveyId) {
    List<QuestionEntity> questions = questionRepository.findBySurveyId(surveyId);
    return questions.stream()
            .flatMap(question -> question.getAnswers().stream())
            .map(answer -> modelMapper.map(answer, AnswerDTO.class))
            .collect(Collectors.toList());
  }

  /**
   * 특정 사용자가 설문에 응답했는지 확인하는 메소드
   * @param surveyId 설문 ID
   * @param respondentId 응답자 ID
   * @return 응답 여부 (true: 응답함, false: 응답하지 않음)
   */
  @Transactional(readOnly = true)
  public boolean hasUserResponded(String surveyId, String respondentId) {
    try {
      SurveyEntity survey = surveyRepository.findById(surveyId)
              .orElseThrow(() -> new RuntimeException("설문조사를 찾을 수 없습니다."));

      // 설문의 모든 질문에 대해 순회
      return survey.getQuestions().stream()
              .anyMatch(question ->
                      question.getAnswers().stream()
                              .anyMatch(answer ->
                                      answer.getRespondentId() != null &&
                                              answer.getRespondentId().equals(respondentId)
                              )
              );
    } catch (Exception e) {
      log.error("응답 확인 중 오류 발생: ", e);
      throw new RuntimeException("응답 확인 중 오류가 발생했습니다.", e);
    }
  }

  /**
   * 설문조사 결과를 조회하는 메소드
   * @param surveyId 설문 ID
   * @return 설문조사 결과 데이터
   */
  @Transactional(readOnly = true)
  public Map<String, Object> getSurveyResults(String surveyId) {
    SurveyEntity survey = surveyRepository.findById(surveyId)
            .orElseThrow(() -> new RuntimeException("설문조사를 찾을 수 없습니다."));

    List<Map<String, Object>> questionResults = survey.getQuestions().stream()
            .map(question -> {
                Map<String, Object> questionData = new HashMap<>();
                questionData.put("questionId", question.getId());
                questionData.put("text", question.getText());
                questionData.put("type", question.getType());

                if (question.getType() == QuestionType.SINGLE_CHOICE || 
                    question.getType() == QuestionType.MULTIPLE_CHOICE) {
                    // 객관식 답변 통계
                    Map<String, Long> answerCounts = new HashMap<>();
                    
                    // 선택지 초기화 (모든 옵션에 대해 0으로 초기화)
                    List<String> options = question.getOptionsList();
                    options.forEach(option -> answerCounts.put(option, 0L));
                    
                    // 실제 답변 카운트
                    question.getAnswers().forEach(answer -> {
                        if (question.getType() == QuestionType.MULTIPLE_CHOICE) {
                            // 다중 선택인 경우 콤마로 구분된 답변을 분리하여 처리
                            String[] selectedOptions = answer.getText().split(",");
                            for (String option : selectedOptions) {
                                answerCounts.merge(option.trim(), 1L, Long::sum);
                            }
                        } else {
                            // 단일 선택인 경우
                            answerCounts.merge(answer.getText(), 1L, Long::sum);
                        }
                    });
                    
                    questionData.put("results", answerCounts);
                } else {
                    // 주관식 답변 목록
                    List<String> textAnswers = question.getAnswers().stream()
                            .map(AnswerEntity::getText)
                            .collect(Collectors.toList());
                    questionData.put("answers", textAnswers);
                }

                return questionData;
            })
            .collect(Collectors.toList());

    Map<String, Object> surveyResults = new HashMap<>();
    surveyResults.put("surveyId", survey.getId());
    surveyResults.put("title", survey.getTitle());
    surveyResults.put("questions", questionResults);
    
    return surveyResults;
  }
}