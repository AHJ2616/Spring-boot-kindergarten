package com.kinder.kindergarten.entity.survey;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "survey_answer")
@Getter
@Setter
public class AnswerEntity {

  @Id
  @Column(name = "answer_id")
  private String id;

  @Column(nullable = false)
  private String text;

  //질문 순서
  private Integer orderNumber;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "question_id")
  private QuestionEntity question;

  // 응답자 정보
  private String respondentId;

  // 선택된 답변인지 여부
  private boolean selected;

}
