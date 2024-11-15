package com.kinder.kindergarten.controller.employee;

import com.kinder.kindergarten.DTO.MultiDTO;
import com.kinder.kindergarten.DTO.employee.EmployeeDTO;
import com.kinder.kindergarten.config.PrincipalDetails;
import com.kinder.kindergarten.DTO.employee.LeaveDTO;
import com.kinder.kindergarten.service.MemberService;
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

    private final MemberService memberService;
    @GetMapping("/request")
    public String leaveRequestForm(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                   Model model) {
        // 직원 인적사항
        MultiDTO multiDTO = memberService.readMember(principalDetails.getMember().getId());

        model.addAttribute("leaveDTO", new LeaveDTO());
        model.addAttribute("multiDTO", multiDTO);
        return "/employee/leave_reg";
    }

    @PostMapping("/request")
    public String submitLeaveRequest(@Valid LeaveDTO leaveDTO,
                                     BindingResult bindingResult,
                                     @ModelAttribute EmployeeDTO employeeDTO,
                                     @AuthenticationPrincipal PrincipalDetails principalDetails) {
        if (bindingResult.hasErrors()) {
            return "/employee/leave_reg"; // 에러가 있을 경우 요청 페이지로 돌아감
        }

        try {
            leaveService.requestLeave(leaveDTO, principalDetails.getMember());
        } catch (IllegalStateException e) {
            bindingResult.rejectValue("le_type", "error.leaveDTO", e.getMessage());
            return "/employee/leave_reg"; // 에러가 있을 경우 요청 페이지로 돌아감
        }

        // 권한에 따른 리다이렉트 경로 설정
        String redirectUrl = null;
        if (principalDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))) {
            redirectUrl = "/admin/dashboard";
        } else if (principalDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_MANAGER"))) {
            redirectUrl = "/manager/dashboard";
        } else if (principalDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_USER"))) {
            redirectUrl = "/teacher/dashboard";
        }

        return "redirect:"+ redirectUrl;
    }

    @GetMapping("/my-leaves")
    public String getMyLeaves(@AuthenticationPrincipal PrincipalDetails principalDetails,
                              Model model) {
        List<LeaveDTO> leaves = leaveService.getLeavesByEmployee(principalDetails.getMember());
        MultiDTO multiDTO = memberService.readMember(principalDetails.getMember().getId());
        model.addAttribute("leaves", leaves);
        model.addAttribute("multiDTO", multiDTO);
        return "/employee/leave_list";
    }

}
