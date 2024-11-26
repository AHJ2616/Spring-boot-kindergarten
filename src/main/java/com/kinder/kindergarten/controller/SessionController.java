package com.kinder.kindergarten.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class SessionController {

    /**
     * 세션 연장 API
     * 세션의 만료 시간을 30분으로 연장합니다.
     */
    @PostMapping("/main/api/extend-session")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> extendSession(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (session.getAttribute("user") == null) {  // 세션이 이미 만료되었거나 유효하지 않으면
                response.put("success", false);
                response.put("message", "세션이 만료되었습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            // 세션 연장
            session.setMaxInactiveInterval(1800);  // 30분으로 연장
            response.put("success", true);
            response.put("message", "세션이 연장되었습니다.");
            response.put("timeLeft", session.getMaxInactiveInterval()); // 연장된 시간 반환
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "세션 연장에 실패했습니다.");
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 세션 상태 확인 API
     * 세션의 남은 시간을 반환합니다.
     */
    @GetMapping("/main/api/check-session")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkSession(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 세션의 남은 시간 (초 단위)
            int timeLeft = session.getMaxInactiveInterval();
            response.put("timeLeft", timeLeft);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "세션을 확인하는 데 실패했습니다.");
            return ResponseEntity.badRequest().body(response);
        }
    }
}
