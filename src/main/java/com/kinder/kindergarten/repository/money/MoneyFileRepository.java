package com.kinder.kindergarten.repository.money;


import com.kinder.kindergarten.entity.money.MoneyFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MoneyFileRepository extends JpaRepository<MoneyFileEntity, String> {

    List<MoneyFileEntity> findByMoneyEntity_moneyId(String moneyId);
}
