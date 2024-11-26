package com.kinder.kindergarten.scheduler;

import com.kinder.kindergarten.repository.material.MaterialOrderHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class MaterialOrderScheduler {

    private final MaterialOrderHistoryRepository materialOrderHistoryRepository;

    @Scheduled(cron = "0 0 0 * * *") // 매일 자정에 실행
    @Transactional
    public void cleanupOldRecords() {
        LocalDateTime cutoffDate = LocalDateTime.now();
        materialOrderHistoryRepository.deleteOldRecords(cutoffDate);
    }
}