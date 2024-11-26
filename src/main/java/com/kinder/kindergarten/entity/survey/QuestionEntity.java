package com.kinder.kindergarten.entity.survey;

import com.github.f4b6a3.ulid.UlidCreator;
import com.kinder.kindergarten.constant.board.QuestionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

@Entity
@Table(name = "survey_question")
@Getter
@Setter
@Transactional
public class QuestionEntity {

    @Id
    @Column(name = "question_id")
    private String id;//1.PK(ULID)

    @Column(nullable = false)
    private String text;//2.질문 내용

    @Column(columnDefinition = "TEXT")
    private String options;//3. 선택지

    @Enumerated(EnumType.STRING)
    private QuestionType type;//4. 답 유형

    private Integer orderNumber;//6. 순서

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id")
    private SurveyEntity survey; //0. FK survey

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnswerEntity> answers = new ArrayList<>();//5. 답의 PK

    public void setId() {
        this.id = UlidCreator.getUlid().toString();
    }

    public void addAnswer(AnswerEntity answer) {
        answers.add(answer);
        answer.setQuestion(this);
    }

    public List<String> getOptionsList() {
        if (options == null || options.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(options.split("\\|"));
    }
}
