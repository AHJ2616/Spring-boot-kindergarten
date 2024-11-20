package com.kinder.kindergarten.repository;

import com.kinder.kindergarten.entity.Member;
import com.kinder.kindergarten.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientAndReadStatusOrderByCreatedAtDesc(Member recipient, boolean readStatus);
    long countByRecipientAndReadStatus(Member recipient, boolean readStatus);
}
