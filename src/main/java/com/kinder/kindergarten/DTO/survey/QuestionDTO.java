package com.kinder.kindergarten.DTO.survey;

import com.kinder.kindergarten.constant.board.QuestionType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class QuestionDTO {
  private String questionId;//1.PK
  private String text;//2.질문 내용
  private List<String> options = new ArrayList<>(); //3.선택지
  private QuestionType type;//4.답 유형
  private List<AnswerDTO> answers = new ArrayList<>();//5.답의 PK
  private Integer orderNumber;//6. 순서
  private List<Long> results;
} 