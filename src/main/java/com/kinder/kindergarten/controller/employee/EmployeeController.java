package com.kinder.kindergarten.controller.employee;


import com.kinder.kindergarten.DTO.employee.*;
import com.kinder.kindergarten.config.PrincipalDetails;
import com.kinder.kindergarten.constant.employee.Role;
import com.kinder.kindergarten.entity.employee.Employee;

import com.kinder.kindergarten.service.FileService;
import com.kinder.kindergarten.service.employee.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import lombok.extern.log4j.Log4j2;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private final FileService fileService; // 파일업로드


    @GetMapping(value = "/new")
    public String EmployeeForm(Model model){
        model.addAttribute("employeeDTO", new EmployeeDTO());
        return "/employee/employee_reg";
    }
    // 끝

    @PostMapping(value = "/new")
    public String newEmployee(@Valid EmployeeDTO employeeDTO, BindingResult bindingResult, Model model) {

        if(employeeDTO.getStatus() == null){employeeDTO.setStatus("재직");}
        if (bindingResult.hasErrors()) {return "/employee/employee_reg";}

        try {
            Employee employee = Employee.createEmployee(employeeDTO, passwordEncoder);
            employeeService.saveEmployee(employee);
        }  catch (Exception e) {
            model.addAttribute("errorMessage", "서버 오류가 발생했습니다.");
            return "/employee/employee_reg";
        }

        return "redirect:/admin/dashboard";
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

    @PostMapping("/update/{field}")
    public ResponseEntity<Map<String, Object>> editEmployee(
            @PathVariable("field") String field,
            @RequestBody Map<String, Object> requestData,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        // 현재 로그인한 사용자의 권한 확인
        boolean isAdmin = principalDetails.getEmployee().getRole() == Role.ROLE_ADMIN;

        // employeeDTO 생성
        EmployeeDTO dto = employeeService.readEmployee(principalDetails.getEmployee().getId());

        // field에 따라 동적으로 처리 (if문 내에서 switch문 사용)
        if (field != null) {
            switch (field) {
                case "name":
                    dto.setName((String) requestData.get("value"));
                    break;
                case "email":
                    dto.setEmail((String) requestData.get("value"));
                    break;
                case "phone":
                    dto.setPhone((String) requestData.get("value"));
                    break;
                case "position":
                    dto.setPosition((String) requestData.get("value"));
                    break;
                case "department":
                    dto.setDepartment((String) requestData.get("value"));
                    break;
                case "status":
                    dto.setStatus((String) requestData.get("value"));
                    break;
                case "address":
                    dto.setAddress((String) requestData.get("value"));
                    break;
                // 다른 필드 추가 가능
                default:
                    throw new IllegalArgumentException("지원하지 않는 필드입니다.");
            }
        }

        // 업데이트 처리
        employeeService.updateEmployee(dto);

        // 성공 응답 반환
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "업데이트 성공");
        return ResponseEntity.ok(response);
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

    // 프로필 이미지 업데이트
    @PostMapping("/update-profile-image")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateProfileImage(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        Map<String, Object> response = new HashMap<>();
        try {
            employeeService.updateProfileImage(principalDetails.getEmployee().getId(), file);
            response.put("success", true);
            response.put("message", "프로필 이미지가 업데이트되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 프로필 이미지 삭제
    @PostMapping("/delete-profile-image")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteProfileImage(
            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        Map<String, Object> response = new HashMap<>();
        try {
            employeeService.deleteProfileImage(principalDetails.getEmployee().getId());
            response.put("success", true);
            response.put("message", "프로필 이미지가 삭제되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 프로필 이미지 조회
    @GetMapping("/profile-image/{fileName}")
    public ResponseEntity<Resource> getProfileImage(@PathVariable String fileName) {
        try {
            Path filePath = Paths.get(fileService.getFullPath(fileName));
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG) // 또는 적절한 이미지 타입
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/employeeList")
    public String listEmployees(Model model) {
        List<EmployeeDTO> employees = employeeService.getAllEmployees();
        model.addAttribute("employees", employees);
        return "/employee/employee_list";
    }

}
