package com.kinder.kindergarten.repository.survey;

import com.kinder.kindergarten.entity.survey.AnswerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnswerRepository extends JpaRepository<AnswerEntity, String> {
    List<AnswerEntity> findByQuestionId(String questionId);
    Optional<AnswerEntity> findByQuestionIdAndRespondentId(String questionId, String respondentId);
} 