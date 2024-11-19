package com.kinder.kindergarten.controller;

import com.kinder.kindergarten.DTO.firebase.RequestDTO;
import com.kinder.kindergarten.entity.FcmTokenEntity;
import com.kinder.kindergarten.entity.Member;
import com.kinder.kindergarten.repository.FcmTokenRepository;
import com.kinder.kindergarten.service.FcmService;
import com.kinder.kindergarten.service.MemberService;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fcm")
@RequiredArgsConstructor
public class FcmRestController {

    private final FcmService fcmService;
    private final MemberService memberService;
    private final FcmTokenRepository fcmTokenRepository;

    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(@RequestBody RequestDTO requestDTO) {
        try {
            fcmService.sendMessage(requestDTO);
            return ResponseEntity.ok("알림이 성공적으로 전송되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("알림 전송 실패: " + e.getMessage());
        }
    }

    @PostMapping("/save-token")
    public ResponseEntity<String> saveToken(@RequestParam Long memberId, @RequestParam String token) {
        try {
            Optional<Member> member = memberService.findById(memberId);
            FcmTokenEntity fcmTokenEntity = new FcmTokenEntity(member.orElse(null), token);
            fcmTokenRepository.save(fcmTokenEntity);
            return ResponseEntity.ok("토큰이 성공적으로 저장되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("토큰 저장 실패: " + e.getMessage());
        }
    }
}
