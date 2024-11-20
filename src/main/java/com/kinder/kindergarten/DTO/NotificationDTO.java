package com.kinder.kindergarten.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Long id;
    private String recipient;  // 수신자 이메일
    private String title;      // 알림 제목
    private String content;    // 알림 내용
    private String type;       // 알림 타입 (LEAVE, APPROVAL 등)
    private String url;        // 클릭시 이동할 URL
    private LocalDateTime createdAt;
    private boolean read;      // 읽음 여부
} 