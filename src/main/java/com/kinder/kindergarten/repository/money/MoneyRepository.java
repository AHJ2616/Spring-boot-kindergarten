package com.kinder.kindergarten.repository.money;

import com.kinder.kindergarten.entity.money.MoneyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.time.LocalDate;
import java.util.List;

public interface MoneyRepository extends JpaRepository<MoneyEntity, String>,
        QuerydslPredicateExecutor<MoneyEntity>, MoneyRepositoryCustom {

    // Count 회계 결재상태에 따른 수 카운트
    Long countByMoneyApproval(String 요청);

    List<MoneyEntity> findByMoneyUseDateBetween(LocalDate startDate, LocalDate endDate);


}
