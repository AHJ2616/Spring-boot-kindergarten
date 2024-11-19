package com.kinder.kindergarten.DTO.survey;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private String questionId;
    private String text;
    private QuestionType type;
    private List<AnswerDTO> answers = new ArrayList<>();
    private Integer orderNumber;
    @JsonIgnore
    private List<String> options;
} 