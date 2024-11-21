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
    private String surveyId;//1.PK
    private String title;//2.제목
    private String description;//3.설명
    private List<QuestionDTO> questions = new ArrayList<>();//4.질문들 
    private LocalDateTime regiDate; //5.등록일
    private LocalDateTime modifiedDate; //6.수정일
} 