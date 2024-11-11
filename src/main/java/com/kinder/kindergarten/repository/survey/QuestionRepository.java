package com.kinder.kindergarten.repository.survey;

import com.kinder.kindergarten.entity.survey.QuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<QuestionEntity, String> {
    List<QuestionEntity> findBySurveyId(String surveyId);
} 