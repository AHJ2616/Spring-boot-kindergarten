package com.kinder.kindergarten.controller;

import com.kinder.kindergarten.DTO.firebase.RequestDTO;
import com.kinder.kindergarten.service.FcmService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/fcm")
@RequiredArgsConstructor
public class FcmRestController {
    
    private final FcmService fcmService;

    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(@RequestBody RequestDTO requestDTO) {
        try {
            fcmService.sendMessage(requestDTO);
            return ResponseEntity.ok("알림이 성공적으로 전송되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("알림 전송 실패: " + e.getMessage());
        }
    }
}
