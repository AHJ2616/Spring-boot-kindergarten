package com.kinder.kindergarten.repository.material;

import com.kinder.kindergarten.entity.material.MaterialOrderHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface MaterialOrderHistoryRepository extends JpaRepository<MaterialOrderHistoryEntity, String> {

    List<MaterialOrderHistoryEntity> findByStatusAndDeletionDateBefore(String status, LocalDateTime date);

    @Modifying
    @Query("DELETE FROM MaterialOrderHistoryEntity m WHERE m.deletionDate <= :date")
    void deleteOldRecords(LocalDateTime date);
}