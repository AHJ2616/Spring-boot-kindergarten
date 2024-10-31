package com.kinder.kindergarten.controller.Employee;

import com.kinder.kindergarten.config.PrincipalDetails;
import com.kinder.kindergarten.DTO.Employee.AttendanceDTO;
import com.kinder.kindergarten.service.Employee.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/check-in")
    @ResponseBody
    public ResponseEntity<AttendanceDTO> checkIn(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        AttendanceDTO attendance = attendanceService.checkIn(principalDetails.getEmployee());
        return ResponseEntity.ok(attendance);
    }

    @PostMapping("/check-out")
    @ResponseBody
    public ResponseEntity<AttendanceDTO> checkOut(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        AttendanceDTO attendance = attendanceService.checkOut(principalDetails.getEmployee());
        return ResponseEntity.ok(attendance);
    }

    @GetMapping("/my-records")
    public String getMyAttendance(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                  Model model) {
        List<AttendanceDTO> records = attendanceService.getMonthlyAttendance(principalDetails.getEmployee());
        model.addAttribute("records", records);
        return "employee/records";
    }
}
