package com.kinder.kindergarten.service;

import com.kinder.kindergarten.DTO.NotificationDTO;
import com.kinder.kindergarten.entity.Member;
import com.kinder.kindergarten.entity.Notification;
import com.kinder.kindergarten.repository.MemberRepository;
import com.kinder.kindergarten.repository.NotificationRepository;
import com.kinder.kindergarten.repository.employee.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationRepository notificationRepository;

    private final EmployeeRepository employeeRepository;

    private final MemberRepository memberRepository;

    public void sendNotification(String recipientEmail, String title, String content, String type, String url) {
        // DB에 알림 저장
        Member recipient = memberRepository.findByEmail(recipientEmail);
        Notification notification = Notification.builder()
                .recipient(recipient)
                .title(title)
                .content(content)
                .type(type)
                .url(url)
                .createdAt(LocalDateTime.now())
                .readStatus(false)
                .build();
        notificationRepository.save(notification);

        // 웹소켓으로 실시간 알림 전송
        NotificationDTO notificationDTO = convertToDTO(notification);
        messagingTemplate.convertAndSendToUser(
                recipientEmail,
                "/queue/notifications",
                notificationDTO
        );
    }

    public List<NotificationDTO> getUnreadNotifications(String email) {
        Member member = memberRepository.findByEmail(email);
        return notificationRepository.findByRecipientAndReadStatusOrderByCreatedAtDesc(member, false)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private NotificationDTO convertToDTO(Notification notification) {
        return NotificationDTO.builder()
                .id(notification.getId())
                .recipient(notification.getRecipient().getEmail())
                .title(notification.getTitle())
                .content(notification.getContent())
                .type(notification.getType())
                .url(notification.getUrl())
                .createdAt(notification.getCreatedAt())
                .read(notification.isReadStatus())
                .build();
    }

    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("알림을 찾을 수 없습니다."));
        notification.setReadStatus(true);
        notificationRepository.save(notification);
    }

    public void markAllAsRead(String email) {
        Member member = memberRepository.findByEmail(email);
        List<Notification> unreadNotifications =
                notificationRepository.findByRecipientAndReadStatusOrderByCreatedAtDesc(member, false);
        unreadNotifications.forEach(notification -> notification.setReadStatus(true));
        notificationRepository.saveAll(unreadNotifications);
    }

    public long getUnreadCount(String email) {
        Member member = memberRepository.findByEmail(email);
        return notificationRepository.countByRecipientAndReadStatus(member, false);
    }
}
