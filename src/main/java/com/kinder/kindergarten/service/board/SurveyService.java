package com.kinder.kindergarten.service.board;

import com.github.f4b6a3.ulid.UlidCreator;
import com.kinder.kindergarten.DTO.survey.SurveyDTO;
import com.kinder.kindergarten.entity.survey.SurveyEntity;
import com.kinder.kindergarten.entity.survey.QuestionEntity;
import com.kinder.kindergarten.entity.survey.AnswerEntity;
import com.kinder.kindergarten.repository.survey.SurveyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
            log.info("Creating survey with data: {}", surveyDTO);
            
            // 새로운 SurveyEntity 생성
            SurveyEntity survey = new SurveyEntity();
            
            // ID 설정
            String surveyId = UlidCreator.getUlid().toString();
            survey.setId(surveyId);
            
            // 기본 정보 설정
            survey.setTitle(surveyDTO.getTitle());
            survey.setDescription(surveyDTO.getDescription());
            
            // 질문 처리
            if (surveyDTO.getQuestions() != null) {
                surveyDTO.getQuestions().forEach(questionDTO -> {
                    QuestionEntity question = new QuestionEntity();
                    question.setId(UlidCreator.getUlid().toString());
                    question.setText(questionDTO.getText());
                    question.setType(questionDTO.getType());
                    question.setOrderNumber(questionDTO.getOrderNumber());
                    
                    // 답변 처리
                    if (questionDTO.getAnswers() != null) {
                        questionDTO.getAnswers().forEach(answerDTO -> {
                            AnswerEntity answer = new AnswerEntity();
                            answer.setId(UlidCreator.getUlid().toString());
                            answer.setText(answerDTO.getText());
                            answer.setOrderNumber(answerDTO.getOrderNumber());
                            answer.setSelected(false);
                            
                            question.addAnswer(answer);
                        });
                    }
                    
                    survey.addQuestion(question);
                });
            }
            
            // 저장
            SurveyEntity savedSurvey = surveyRepository.save(survey);
            
            // DTO로 변환하여 반환
            return modelMapper.map(savedSurvey, SurveyDTO.class);
            
        } catch (Exception e) {
            log.error("설문조사 생성 중 오류 발생: ", e);
            throw new RuntimeException("설문조사 생성 중 오류가 발생했습니다: " + e.getMessage());
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
    
    public SurveyDTO getSurveyByBoardId(String boardId) {
        SurveyEntity surveyEntity = surveyRepository.findSurveyEntityByBoardId(boardId);
        
        if (surveyEntity == null) {
            return null;
        }
        
        return modelMapper.map(surveyEntity, SurveyDTO.class);
    }
}