package com.kinder.kindergarten.controller;

import com.kinder.kindergarten.DTO.NotificationDTO;
import com.kinder.kindergarten.config.PrincipalDetails;
import com.kinder.kindergarten.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/unread")
    public ResponseEntity<List<NotificationDTO>> getUnreadNotifications(
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        List<NotificationDTO> notifications = notificationService.getUnreadNotifications(
                principalDetails.getMember().getEmail());
        return ResponseEntity.ok(notifications);
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        notificationService.markAllAsRead(principalDetails.getMember().getEmail());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getUnreadCount(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        long count = notificationService.getUnreadCount(principalDetails.getMember().getEmail());
        return ResponseEntity.ok(count);
    }
}
