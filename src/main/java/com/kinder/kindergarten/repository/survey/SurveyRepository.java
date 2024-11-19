package com.kinder.kindergarten.repository.survey;

import com.kinder.kindergarten.entity.survey.SurveyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SurveyRepository extends JpaRepository<SurveyEntity, String> {
} 