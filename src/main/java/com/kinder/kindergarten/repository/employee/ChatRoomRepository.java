package com.kinder.kindergarten.repository.employee;

import com.kinder.kindergarten.entity.employee.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @Query("SELECT cr FROM ChatRoom cr WHERE :memberId IN (SELECT m.id FROM cr.participants m)")
    List<ChatRoom> findByParticipantId(Long memberId);

    @Query("SELECT cr FROM ChatRoom cr WHERE SIZE(cr.participants) = 2 AND :member1Id IN (SELECT m.id FROM cr.participants m) AND :member2Id IN (SELECT m.id FROM cr.participants m)")
    Optional<ChatRoom> findByTwoParticipants(Long member1Id, Long member2Id);
}
