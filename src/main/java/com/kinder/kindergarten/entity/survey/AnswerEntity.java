package com.kinder.kindergarten.entity.survey;

import org.hibernate.annotations.BatchSize;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "survey_answer")
@Getter
@Setter
@BatchSize(size = 10)
@Transactional
public class AnswerEntity {

  @Id
  @Column(name = "answer_id")
  private String id;// 1.PK(ULID)

  @Column(nullable = false)
  private String text;//2. 답


  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "question_id")
  private QuestionEntity question;//4.질문 FK

  // 응답자 정보
  private String respondentId; //3.설문 응답자

}
