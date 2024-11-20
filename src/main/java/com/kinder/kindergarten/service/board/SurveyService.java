package com.kinder.kindergarten.service.board;

import com.github.f4b6a3.ulid.UlidCreator;
import com.kinder.kindergarten.DTO.survey.SurveyDTO;
import com.kinder.kindergarten.entity.survey.AnswerEntity;
import com.kinder.kindergarten.entity.survey.QuestionEntity;
import com.kinder.kindergarten.entity.survey.SurveyEntity;
import com.kinder.kindergarten.repository.survey.SurveyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class SurveyService {
    
    private final SurveyRepository surveyRepository;
    private final ModelMapper modelMapper;


    // 설문조사 생성
    public SurveyDTO createSurvey(SurveyDTO surveyDTO) {
        try {
            SurveyEntity surveyEntity = new SurveyEntity();
            surveyEntity.setId(UlidCreator.getUlid().toString());
            surveyEntity = modelMapper.map(surveyDTO, SurveyEntity.class);
            
            // BoardEntity 참조 설정
            
            SurveyEntity savedEntity = surveyRepository.save(surveyEntity);
            return modelMapper.map(savedEntity, SurveyDTO.class);
        } catch (Exception e) {
            log.error("설문조사 생성 중 오류 발생: ", e);
            throw new RuntimeException("설문조사 생성 중 오류가 발생했습니다", e);
        }
    }
    
    // 설문조사 조회
    @Transactional(readOnly = true)
    public SurveyDTO getSurvey(String surveyId) {
        SurveyEntity survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new RuntimeException("설문조사를 찾을 수 없습니다."));
        return modelMapper.map(survey, SurveyDTO.class);
    }
    
    // 모든 설문조사 조회
    @Transactional(readOnly = true)
    public List<SurveyDTO> getAllSurveys() {
        List<SurveyEntity> surveys = surveyRepository.findAll();
        return surveys.stream()
                .map(survey -> modelMapper.map(survey, SurveyDTO.class))
                .collect(Collectors.toList());
    }
    
    // 설문조사 수정
    public SurveyDTO updateSurvey(String surveyId, SurveyDTO surveyDTO) {
        SurveyEntity survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new RuntimeException("설문조사를 찾을 수 없습니다."));
        
        modelMapper.map(surveyDTO, survey);
        SurveyEntity updatedSurvey = surveyRepository.save(survey);
        return modelMapper.map(updatedSurvey, SurveyDTO.class);
    }
    
    // 설문조사 삭제
    public void deleteSurvey(String surveyId) {
        surveyRepository.deleteById(surveyId);
    }
    
    
    // 설문 응답 저장
    public void saveSurveyResponses(String surveyId, List<Map<String, Object>> responses) {
        SurveyEntity survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new RuntimeException("설문조사를 찾을 수 없습니다."));

        responses.forEach(response -> {
            String questionId = (String) response.get("questionId");
            String answerId = (String) response.get("answerId");
            String respondentId = (String) response.get("respondentId");

            // 질문 찾기
            QuestionEntity question = survey.getQuestions().stream()
                    .filter(q -> q.getId().equals(questionId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("질문을 찾을 수 없습니다."));

            // 답변 찾기 및 업데이트
            AnswerEntity answer = question.getAnswers().stream()
                    .filter(a -> a.getId().equals(answerId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("답변을 찾을 수 없습니다."));

            // 응답 정보 설정
            answer.setSelected(true);
            answer.setRespondentId(respondentId);
        });

        // 변경사항 저장
        surveyRepository.save(survey);
    }
}