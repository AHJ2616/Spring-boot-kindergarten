package com.kinder.kindergarten.controller.employee;

import com.kinder.kindergarten.DTO.employee.EmployeeDTO;
import com.kinder.kindergarten.config.PrincipalDetails;
import com.kinder.kindergarten.DTO.employee.LeaveDTO;
import com.kinder.kindergarten.service.employee.EmployeeService;
import com.kinder.kindergarten.service.employee.LeaveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/leave")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService leaveService;
    private final EmployeeService employeeService;
    @GetMapping("/request/{id}")
    public String leaveRequestForm(@PathVariable("id") Long id, Model model) {
        // 직원 인적사항
        EmployeeDTO employeeDTO = employeeService.readEmployee(id);

        model.addAttribute("leaveDTO", new LeaveDTO());
        model.addAttribute("employeeDTO", employeeDTO);
        return "employee/request";
    }

    @PostMapping("/request")
    public String submitLeaveRequest(@Valid LeaveDTO leaveDTO,
                                     BindingResult bindingResult,
                                     @ModelAttribute EmployeeDTO employeeDTO,
                                     @AuthenticationPrincipal PrincipalDetails principalDetails) {
        if (bindingResult.hasErrors()) {
            return "employee/request"; // 에러가 있을 경우 요청 페이지로 돌아감
        }

        try {
            leaveService.requestLeave(leaveDTO, principalDetails.getEmployee());
        } catch (IllegalStateException e) {
            bindingResult.rejectValue("le_type", "error.leaveDTO", e.getMessage());
            return "employee/request"; // 에러가 있을 경우 요청 페이지로 돌아감
        }

        return "redirect:/";
    }

    @GetMapping("/my-leaves")
    public String getMyLeaves(@AuthenticationPrincipal PrincipalDetails principalDetails,
                              Model model) {
        List<LeaveDTO> leaves = leaveService.getLeavesByEmployee(principalDetails.getEmployee());
        model.addAttribute("leaves", leaves);
        return "employee/my-leaves";
    }

    // 관리자용 - 휴가 승인/반려 처리
//    @PreAuthorize("hasRole('ADMIN')")
//    @PostMapping("/process/{leaveId}")
//    @ResponseBody
//    public ResponseEntity<?> processLeave(@PathVariable Long leaveId,
//                                          @RequestParam(required = false) String rejectReason) {
//        leaveService.processLeave(leaveId, status, rejectReason);
//        return ResponseEntity.ok().build();
//    }
//
//    // 관리자용 - 전체 휴가 신청 목록
//    @PreAuthorize("hasRole('ADMIN')")
//    @GetMapping("/admin/all")
//    public String getAllLeaves(Model model) {
//        List<LeaveDTO> allLeaves = leaveService.getAllLeaves();
//        model.addAttribute("leaves", allLeaves);
//        return "employee/leave/admin-leave-list";
//    }
}
