package com.kinder.kindergarten.DTO.survey;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AnswerDTO {
    private String answerId;//1. PK
    private String text;//2. 답
    private String respondentId; //3. 설문 응답자
    private String questionId;//4. 질문 FK
} 