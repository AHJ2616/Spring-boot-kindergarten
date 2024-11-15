package com.kinder.kindergarten.controller.employee;

import com.kinder.kindergarten.config.PrincipalDetails;
import com.kinder.kindergarten.constant.employee.ApprovalStatus;
import com.kinder.kindergarten.constant.employee.ApprovalType;
import com.kinder.kindergarten.entity.Member;
import com.kinder.kindergarten.entity.employee.Employee;
import com.kinder.kindergarten.service.MemberService;
import com.kinder.kindergarten.service.employee.ApprovalService;
import com.kinder.kindergarten.service.employee.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/approval")
@RequiredArgsConstructor
public class ApprovalController {

    private final ApprovalService approvalService;
    private final EmployeeService employeeService;


    @GetMapping("/pending")
    public String getPendingApprovals(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                      Model model) {
        Member member = principalDetails.getMember();
        model.addAttribute("pendingApprovals", approvalService.getPendingApprovals(member));
        return "/employee/approval_pending";
    }

    @PostMapping("/process/{id}")
    @ResponseBody
    public ResponseEntity<?> processApproval(@PathVariable Long id,
                                             @RequestParam ApprovalStatus status,
                                             @RequestParam(required = false) String rejectReason) {
        approvalService.processApproval(id, status, rejectReason);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/list")
    public String getApprovalList(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                  Model model) {
        model.addAttribute("myRequests",
                approvalService.getRequestedApprovals(principalDetails.getMember()));
        return "/employee/approval_list";
    }

    @GetMapping("/request")
    public String showRequestForm(Model model) {
        // 결재자 목록 조회 (예: 부서장 이상)
        List<Employee> approvers = employeeService.findApprovers();
        model.addAttribute("approvers", approvers);
        return "/employee/approval_request";
    }

//    @PostMapping("/submit")
//    public String submitApproval(@RequestParam Map<String, String> params,
//                                 @AuthenticationPrincipal PrincipalDetails principalDetails) {
//        Member requester = principalDetails.getMember();
//        //Employee approver = memberService.getEmployeeEntity(Long.parseLong(params.get("approverId")));
//
//        // 결재 요청 생성
//        approvalService.createApproval(requester, approver, params.get("title"),
//                params.get("content"), ApprovalType.GENERAL);
//
//        return "redirect:/approval/list";
//    }
}
