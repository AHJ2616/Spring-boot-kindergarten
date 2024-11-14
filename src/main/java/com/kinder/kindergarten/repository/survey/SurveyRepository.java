package com.kinder.kindergarten.repository.survey;

import com.kinder.kindergarten.entity.survey.SurveyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SurveyRepository extends JpaRepository<SurveyEntity, String> {
    // Spring Data JPA의 명명 규칙을 따르는 메서드로 변경
    SurveyEntity findSurveyEntityByBoardId(String boardId);
} 