package com.kinder.kindergarten.repository.money;
import com.kinder.kindergarten.DTO.money.MoneySearchDTO;
import com.kinder.kindergarten.entity.money.MoneyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MoneyRepositoryCustom {

    Page<MoneyEntity> getMoneyPage(MoneySearchDTO moneySearchDTO, Pageable pageable);
}
