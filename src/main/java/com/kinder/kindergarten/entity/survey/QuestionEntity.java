package com.kinder.kindergarten.entity.survey;

import com.github.f4b6a3.ulid.UlidCreator;
import com.kinder.kindergarten.constant.board.QuestionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "survey_question")
@Getter
@Setter
public class QuestionEntity {

    @Id
    @Column(name = "question_id")
    private String id;

    @Column(nullable = false)
    private String text;

    @Enumerated(EnumType.STRING)
    private QuestionType type;

    private Integer orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id")
    private SurveyEntity survey;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnswerEntity> answers = new ArrayList<>();

    // ID 설정을 위한 메소드 추가
    public void setId(String id) {
          this.id = UlidCreator.getUlid().toString();
    }

    // 양방향 관계 설정을 위한 편의 메소드
    public void addAnswer(AnswerEntity answer) {
        answers.add(answer);
        answer.setQuestion(this);
    }
}
