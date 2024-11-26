package com.kinder.kindergarten.controller.board;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kinder.kindergarten.DTO.survey.SurveyDTO;
import com.kinder.kindergarten.annotation.CurrentUser;
import com.kinder.kindergarten.config.PrincipalDetails;
import com.kinder.kindergarten.service.board.SurveyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;


@Controller
@RequestMapping(value="/survey")
@Log4j2
@RequiredArgsConstructor
public class SurveyController {

  private final SurveyService surveyService;

  @GetMapping("/list")
  public String listSurvey(Model model,
                           @CurrentUser PrincipalDetails principalDetails,
                           @RequestParam(value="page", defaultValue="0") int page) {

    Pageable pageable = PageRequest.of(page, 10); // 한 페이지당 10개씩
    Page<SurveyDTO> surveys = surveyService.getAllSurveys(pageable);

    model.addAttribute("surveys", surveys);
    model.addAttribute("maxPage", 5); // 페이지 네비게이션에 보여줄 최대 페이지 수
    model.addAttribute("userRole",principalDetails.getMember().getRole());
    return "survey/surveyList";
  }

  @GetMapping(value="{surveyId}")
  public String getMethodName(@PathVariable String surveyId,
                              @CurrentUser PrincipalDetails principalDetails,
                              Model model) {
    SurveyDTO surveyDTO = surveyService.getSurvey(surveyId);
    String respondentId = principalDetails.getUsername();
    surveyDTO.setIsCompleted(surveyService.hasUserResponded(surveyId, respondentId));//설문참여 여부
    model.addAttribute("survey", surveyDTO);
    model.addAttribute("questions", surveyDTO.getQuestions());
    log.info("질문내용 : "+surveyDTO.getQuestions());
    log.info("작성완료 유뮤 :" + surveyDTO.getIsCompleted());
    model.addAttribute("userRole",principalDetails.getMember().getRole());

    return "survey/surveyGet";
  }

  @GetMapping(value = "/create")
  public String createSurveyForm(Model model,@CurrentUser PrincipalDetails principalDetails) {
    model.addAttribute("survey", new SurveyDTO());
    model.addAttribute("userRole",principalDetails.getMember().getRole());
    return "/survey/surveyWrite";
  }

  @PostMapping(value="/create")
  @ResponseBody
  public Map<String, Object> createSurvey(@CurrentUser PrincipalDetails principalDetails,
                                          @RequestBody SurveyDTO surveyDTO) {
    try {
      String userName = principalDetails.getMember().getName();
      surveyDTO.setWriter(userName);

      surveyService.createSurvey(surveyDTO);

      return Map.of(
              "success", true,
              "message", "설문이 성공적으로 생성되었습니다.",
              "redirectUrl", "/survey/list"
      );
    } catch (Exception e) {
      return Map.of(
              "success", false,
              "message", "설문 생성 중 오류가 발생했습니다: " + e.getMessage()
      );
    }
  }

  @PostMapping(value="/submit")
  @ResponseBody
  public Map<String, String> submitSurvey(@RequestBody Map<String, Object> requestData,
                                           @CurrentUser PrincipalDetails principalDetails) {
    try {
      String surveyId = (String) requestData.get("surveyId");
      List<Map<String, Object>> answers = (List<Map<String, Object>>) requestData.get("answers");
      String respondentId = principalDetails.getUsername();

      surveyService.submitSurveyResponse(surveyId, answers, respondentId);

      return Map.of(
              "status", "success",
              "message", "설문이 성공적으로 제출되었습니다.",
              "redirectUrl", "/survey/list"
      );
    } catch (Exception e) {
      return Map.of(
              "status", "error",
              "message", "설문 제출 중 오류가 발생했습니다: " + e.getMessage()
      );
    }
  }

  @GetMapping("/{surveyId}/results")
  @ResponseBody
  public Map<String, Object> getSurveyResults(@PathVariable String surveyId) {
    try {
        Map<String, Object> results = surveyService.getSurveyResults(surveyId);
        return Map.of(
            "status", "success",
            "data", results
        );
    } catch (Exception e) {
        return Map.of(
            "status", "error",
            "message", e.getMessage()
        );
    }
  }

}
