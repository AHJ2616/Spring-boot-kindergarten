package com.kinder.kindergarten.entity.survey;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import com.github.f4b6a3.ulid.UlidCreator;
import com.kinder.kindergarten.entity.TimeEntity;
import com.kinder.kindergarten.entity.board.BoardEntity;

@Entity
@Table(name = "survey")
@Getter
@Setter
public class SurveyEntity extends TimeEntity {
    
    @Id
    @Column(name = "survey_id")
    private String id;

    //설문조사 제목
    @Column(nullable = false)
    private String title;

    //설문조사 전체내용
    @Column(length = 1000)
    private String description;
    
    //설문조사 질문 list 
    @OneToMany(mappedBy = "survey", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuestionEntity> questions = new ArrayList<>();

    @Column(name = "board_id")
    private String boardId;

    // ID 설정을 위한 메소드 추가
    public void setId(String id) {
        this.id = UlidCreator.getUlid().toString();
    }

    // 양방향 관계 설정을 위한 편의 메소드
    public void addQuestion(QuestionEntity question) {
        questions.add(question);
        question.setSurvey(this);
    }
}
