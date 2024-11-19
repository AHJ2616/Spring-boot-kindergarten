package com.kinder.kindergarten.DTO.survey;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class SurveyDTO {
    private String surveyId;
    private String title;
    private String description;
    private List<QuestionDTO> questions = new ArrayList<>();
    private LocalDateTime regiDate;
    private LocalDateTime modifiedDate;
} 