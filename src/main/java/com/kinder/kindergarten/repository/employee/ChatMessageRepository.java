package com.kinder.kindergarten.repository.employee;

import com.kinder.kindergarten.entity.employee.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatRoomIdOrderBySendTimeAsc(Long chatRoomId);
    List<ChatMessage> findBySenderIdOrReceiverIdOrderBySendTimeDesc(Long senderId, Long receiverId);
    List<ChatMessage> findByChatRoomIdAndReceiverIdAndIsReadFalse(Long chatRoomId, Long receiverId);
    List<ChatMessage> findByChatRoomIdAndReceiverIdOrderBySendTimeDesc(Long chatRoomId, Long receiverId);
    Long countByReceiverIdAndIsReadFalse(Long receiverId);
}
