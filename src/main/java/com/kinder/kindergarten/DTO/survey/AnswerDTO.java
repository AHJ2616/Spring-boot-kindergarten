package com.kinder.kindergarten.DTO.survey;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AnswerDTO {
    private String answerId;
    private String text;
    private Integer orderNumber;
    private boolean selected;
    private String respondentId;
} 