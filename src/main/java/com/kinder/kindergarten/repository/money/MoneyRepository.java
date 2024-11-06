package com.kinder.kindergarten.repository.money;


import com.kinder.kindergarten.entity.money.MoneyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface MoneyRepository extends JpaRepository<MoneyEntity, String>,
        QuerydslPredicateExecutor<MoneyEntity>, MoneyRepositoryCustom {
}
