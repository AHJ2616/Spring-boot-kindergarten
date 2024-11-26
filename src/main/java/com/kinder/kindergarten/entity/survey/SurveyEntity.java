package com.kinder.kindergarten.entity.survey;

import com.kinder.kindergarten.entity.TimeEntity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "survey")
@Getter
@Setter
public class SurveyEntity extends TimeEntity {

  @Id
  @Column(name = "survey_id")
  private String id;//1.PK(ULID)

  //설문조사 제목
  @Column(nullable = false)
  private String title;//2.제목

  //설문조사 전체내용
  @Column(length = 1000)
  private String description;//3.설명

  @Column
  private String writer; //5.작성자 이름

  //설문조사 질문 list
  @OneToMany(mappedBy = "survey", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<QuestionEntity> questions = new ArrayList<>();
  //4.질문

  // 양방향 관계 설정을 위한 편의 메소드
  public void addQuestion(QuestionEntity question) {
    questions.add(question);
    question.setSurvey(this);
  }
}
