package com.kinder.kindergarten.controller.Employee;

import com.kinder.kindergarten.config.PrincipalDetails;
import com.kinder.kindergarten.DTO.Employee.LeaveDTO;
import com.kinder.kindergarten.service.Employee.LeaveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/leave")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService leaveService;

    @GetMapping("/request")
    public String leaveRequestForm(Model model) {
        model.addAttribute("leaveDTO", new LeaveDTO());
        return "employee/request";
    }

    @PostMapping("/request")
    public String submitLeaveRequest(@Valid LeaveDTO leaveDTO,
                                     @AuthenticationPrincipal PrincipalDetails principalDetails) {
        leaveService.requestLeave(leaveDTO, principalDetails.getEmployee());
        return "redirect:/";
    }

    @GetMapping("/my-leaves")
    public String getMyLeaves(@AuthenticationPrincipal PrincipalDetails principalDetails,
                              Model model) {
        List<LeaveDTO> leaves = leaveService.getLeavesByEmployee(principalDetails.getEmployee());
        model.addAttribute("leaves", leaves);
        return "leave/my-leaves";
    }
}
