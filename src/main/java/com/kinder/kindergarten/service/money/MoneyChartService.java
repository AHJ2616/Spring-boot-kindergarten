package com.kinder.kindergarten.service.money;

import com.kinder.kindergarten.constant.money.MoneyStatus;
import com.kinder.kindergarten.entity.money.MoneyEntity;
import com.kinder.kindergarten.repository.money.MoneyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MoneyChartService {

    private final MoneyRepository moneyRepository;

    public Map<String, Object> getMonthlyChartData(YearMonth yearMonth) {
        // 해당 월의 시작일과 마지막일 계산
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        // 데이터 조회
        List<MoneyEntity> monthlyData = moneyRepository.findByMoneyUseDateBetween(startDate, endDate);

        // 일별 데이터 초기화
        Map<Integer, Integer> dailyIncome = new HashMap<>();
        Map<Integer, Integer> dailyExpenditure = new HashMap<>();

        // 모든 날짜에 대해 0으로 초기화
        for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
            dailyIncome.put(day, 0);
            dailyExpenditure.put(day, 0);
        }

        // 데이터 집계
        monthlyData.forEach(money -> {
            int day = money.getMoneyUseDate().getDayOfMonth();

            if (money.getMoneyStatus() == MoneyStatus.INCOME) {
                dailyIncome.merge(day, money.getMoneyHowMuch(), Integer::sum);
            } else {
                dailyExpenditure.merge(day, money.getMoneyHowMuch(), Integer::sum);
            }
        });

        // 차트 데이터 구성
        List<String> labels = new ArrayList<>();
        List<Integer> incomeData = new ArrayList<>();
        List<Integer> expenditureData = new ArrayList<>();

        for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
            labels.add(day + "일");
            incomeData.add(dailyIncome.get(day));
            expenditureData.add(dailyExpenditure.get(day));
        }

        Map<String, Object> chartData = new HashMap<>();
        chartData.put("labels", labels);
        chartData.put("incomeData", incomeData);
        chartData.put("expenditureData", expenditureData);

        return chartData;
    }

}