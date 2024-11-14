package com.kinder.kindergarten.scheduler;

import com.kinder.kindergarten.service.material.MaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MaterialOrderScheduler {

    private final MaterialService materialService;

    @Scheduled(cron = "0 0 0 * * *") // 매일 자정에 실행
    public void cleanupOldOrderHistories() {
        materialService.deleteOldOrderHistories();
    }
}