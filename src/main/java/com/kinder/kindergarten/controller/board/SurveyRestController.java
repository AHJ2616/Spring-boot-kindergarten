package com.kinder.kindergarten.controller.board;

import com.kinder.kindergarten.DTO.survey.SurveyDTO;
import com.kinder.kindergarten.service.board.SurveyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/survey")
@RequiredArgsConstructor
@Slf4j
public class SurveyRestController {

    private final SurveyService surveyService;

    // 설문조사 조회
    @GetMapping("/{surveyId}")
    public ResponseEntity<SurveyDTO> getSurvey(@PathVariable String surveyId) {
        try {
            SurveyDTO survey = surveyService.getSurvey(surveyId);
            return ResponseEntity.ok(survey);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 모든 설문조사 조회
    @GetMapping(value = "/research")
    public ResponseEntity<List<SurveyDTO>> getAllSurveys() {
        try {
            List<SurveyDTO> surveys = surveyService.getAllSurveys();
            return ResponseEntity.ok(surveys);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 설문조사 수정
    @PutMapping("/{surveyId}")
    public ResponseEntity<SurveyDTO> updateSurvey(
            @PathVariable String surveyId,
            @RequestBody SurveyDTO surveyDTO) {
        try {
            SurveyDTO updatedSurvey = surveyService.updateSurvey(surveyId, surveyDTO);
            return ResponseEntity.ok(updatedSurvey);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 설문조사 삭제
    @DeleteMapping("/{surveyId}")
    public ResponseEntity<Map<String, String>> deleteSurvey(@PathVariable String surveyId) {
        try {
            surveyService.deleteSurvey(surveyId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 설문 응답 저장
    @PostMapping("/{surveyId}/responses")
    public ResponseEntity<?> saveSurveyResponses(
            @PathVariable String surveyId,
            @RequestBody List<Map<String, Object>> responses) {
        try {
            surveyService.saveSurveyResponses(surveyId, responses);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
