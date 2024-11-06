package com.kinder.kindergarten.controller.Employee;


import com.kinder.kindergarten.DTO.Employee.*;
import com.kinder.kindergarten.config.PrincipalDetails;
import com.kinder.kindergarten.entity.Employee.Employee;
import com.kinder.kindergarten.service.Employee.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RequestMapping("/employee")
@Controller
@RequiredArgsConstructor
@Log4j2
public class EmployeeController {

    private final EmployeeService employeeService; // 직원관리
    private final CertificateService certificateService; // 자격증관리
    private final AttendanceService attendanceService; // 근태관리
    private final LeaveService leaveService; // 휴가관리
    private final EducationService educationService; // 교육이력
    private final PasswordEncoder passwordEncoder; // 비밀번호 인코딩


    @GetMapping(value = "/new")
    public String EmployeeForm(Model model){
        model.addAttribute("employeeDTO", new EmployeeDTO());
        return "employee/new";
    }
    // 끝

    @PostMapping(value = "/new")
    public String newEmployee(@Valid EmployeeDTO employeeDTO, BindingResult bindingResult, Model model) {
        System.out.println(employeeDTO);
        if(employeeDTO.getStatus() == null){employeeDTO.setStatus("재직");}

        if (bindingResult.hasErrors()) {return "employee/new";}

        try {
            Employee employee = Employee.createEmployee(employeeDTO, passwordEncoder);
            employeeService.saveEmployee(employee);
        }  catch (Exception e) {
            model.addAttribute("errorMessage", "서버 오류가 발생했습니다.");
            return "employee/new";
        }

        return "redirect:main/login";
    }
    // 끝

    @GetMapping("/mypage/{id}")
    public String getEmployeeById(@PathVariable("id") Long id, @AuthenticationPrincipal PrincipalDetails principalDetails, Model model) {
        // 직원 인적사항
        EmployeeDTO employeeDTO = employeeService.readEmployee(id);
        // 직원 자격증리스트
        List<CertificateDTO> certificates = certificateService.getCertificatesByEmployee(principalDetails.getEmployee());
        // 직원 근태현황
        List<AttendanceDTO> records = attendanceService.getMonthlyAttendance(principalDetails.getEmployee());
        // 직원 휴가현황
        List<LeaveDTO> leaves = leaveService.getLeavesByEmployee(principalDetails.getEmployee());
        // 교육이력현황
        List<EducationDTO> history = educationService.getEducationHistory(principalDetails.getEmployee());


        model.addAttribute("employeeDTO", employeeDTO); // 직원정보
        model.addAttribute("certificates", certificates); // 자격증정보
        model.addAttribute("records", records); // 근태정보
        model.addAttribute("leaves", leaves); // 휴가정보
        model.addAttribute("history", history);
        // id에 해당하는 직원 정보 반환
        return "employee/mypage";
    }
    // 마이페이지 끝

    @PostMapping("/update")
    public String editEmployee(@ModelAttribute EmployeeDTO employeeDTO){
        employeeService.updateEmployee(employeeDTO);
        return "redirect:/employee/mypage/" + employeeDTO.getId();
    }

    // 이메일 중복 검사
    @GetMapping("/check-email")
    @ResponseBody
    public Map<String, Object> checkEmail(@RequestParam("email") String email) {
        Map<String, Object> response = new HashMap<>();
        boolean exists = employeeService.isEmailExists(email);
        response.put("exists", exists);
        return response;
    }

    // 전화번호 중복 검사
    @GetMapping("/check-phone")
    @ResponseBody
    public Map<String, Object> checkPhone(@RequestParam("phone") String phone) {
        Map<String, Object> response = new HashMap<>();
        boolean exists = employeeService.isPhoneExists(phone);
        response.put("exists", exists);
        return response;
    }

    @GetMapping("/admin/employees")
    public String listEmployees(Model model) {
        List<EmployeeDTO> employees = employeeService.getAllEmployees();
        model.addAttribute("employees", employees);
        return "clist";
    }

    @GetMapping("/edit/{id}")
    public String editEmployeeForm(@PathVariable Long id, Model model) {
        EmployeeDTO employeeDTO = employeeService.getEmployeeById(id);
        model.addAttribute("employeeDTO", employeeDTO);
        return "employee/edit";
    }

    @PostMapping("/edit")
    public String updateEmployee(@Valid @ModelAttribute EmployeeDTO employeeDTO,
                                 BindingResult result) {
        if (result.hasErrors()) {
            return "employee/edit";
        }
        employeeService.updateEmployee(employeeDTO);
        return "redirect:/employee/mypage/" + employeeDTO.getId();
    }

    @PostMapping("/delete/{id}")
    public String deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return "redirect:/";
    }

}
