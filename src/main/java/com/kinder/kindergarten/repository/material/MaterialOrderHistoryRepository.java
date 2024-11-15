package com.kinder.kindergarten.repository.material;

import com.kinder.kindergarten.entity.material.MaterialOrderHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface MaterialOrderHistoryRepository extends JpaRepository<MaterialOrderHistory, Long> {

    List<MaterialOrderHistory> findByOrderDateBefore(LocalDateTime date);

/*    @Query("SELECT moh FROM MaterialOrderHistory moh WHERE moh.materialOrderStatus IN ('CANCELED', 'COMPLETED')")
    List<MaterialOrderHistory> findAllCompletedOrCanceled();*/

    @Query("SELECT moh FROM MaterialOrderHistory moh WHERE moh.materialOrderStatus IN (com.kinder.kindergarten.constant.material.MaterialOrderStatus.CANCELED, com.kinder.kindergarten.constant.material.MaterialOrderStatus.COMPLETED)")
    List<MaterialOrderHistory> findAllCompletedOrCanceled();
}