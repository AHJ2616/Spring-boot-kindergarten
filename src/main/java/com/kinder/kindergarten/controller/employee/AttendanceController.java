package com.kinder.kindergarten.controller.employee;

import com.kinder.kindergarten.config.PrincipalDetails;
import com.kinder.kindergarten.DTO.employee.AttendanceDTO;
import com.kinder.kindergarten.service.employee.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/check-in")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkIn(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        Map<String, Object> response = new HashMap<>();
        try {
            AttendanceDTO attendance = attendanceService.checkIn(principalDetails.getMember());
            response.put("success", true);
            response.put("message", "출근이 완료되었습니다.");
            response.put("attendance", attendance);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "출근 처리 중 오류가 발생했습니다.");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/check-out")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkOut(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        Map<String, Object> response = new HashMap<>();
        try {
            AttendanceDTO attendance = attendanceService.checkOut(principalDetails.getMember());
            response.put("success", true);
            response.put("message", "퇴근이 완료되었습니다.");
            response.put("attendance", attendance);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "퇴근 처리 중 오류가 발생했습니다.");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAttendanceStatus(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        Map<String, Object> response = new HashMap<>();
        try {
            AttendanceDTO status = attendanceService.getTodayAttendance(principalDetails.getMember());
            response.put("success", true);
            response.put("attendance", status);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "출근 상태 조회 중 오류가 발생했습니다.");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/my-records")
    public String getMyAttendance(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                  Model model) {
        List<AttendanceDTO> records = attendanceService.getMonthlyAttendance(principalDetails.getMember());
        model.addAttribute("records", records);
        return "attendance_list";
    }


}
