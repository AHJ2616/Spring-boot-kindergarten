package com.kinder.kindergarten.controller.employee;


import com.kinder.kindergarten.DTO.MemberDTO;
import com.kinder.kindergarten.DTO.MultiDTO;
import com.kinder.kindergarten.DTO.employee.*;
import com.kinder.kindergarten.config.PrincipalDetails;
import com.kinder.kindergarten.constant.employee.Role;
import com.kinder.kindergarten.entity.Member;
import com.kinder.kindergarten.entity.employee.Employee;
import com.kinder.kindergarten.service.FileService;
import com.kinder.kindergarten.service.MemberService;
import com.kinder.kindergarten.service.employee.*;
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

import java.math.BigDecimal;
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
    private final MemberService memberService; // 회원값
    private final CertificateService certificateService; // 자격증관리
    private final AttendanceService attendanceService; // 근태관리
    private final LeaveService leaveService; // 휴가관리
    private final EducationService educationService; // 교육이력
    private final PasswordEncoder passwordEncoder; // 비밀번호 인코딩

    private final FileService fileService; // 파일업로드


    // 1. 직원정보 등록
    @GetMapping(value = "/new")
    public String EmployeeForm(Model model){

        MultiDTO multiDTO = new MultiDTO();
        multiDTO.setMemberDTO(new MemberDTO());
        multiDTO.setEmployeeDTO(new EmployeeDTO());

        model.addAttribute("multiDTO", multiDTO);
        return "/employee/employee_reg";
    }

    // 1-1. 직원정보 등록
    @PostMapping(value = "/new")
    public String newEmployee(@ModelAttribute("multiDTO")MultiDTO multiDTO, BindingResult bindingResult, Model model) {

        MemberDTO memberDTO = multiDTO.getMemberDTO();
        EmployeeDTO employeeDTO = multiDTO.getEmployeeDTO();


        if(employeeDTO.getStatus() == null){employeeDTO.setStatus("재직");}
        if (bindingResult.hasErrors()) {return "/employee/employee_reg";}

        try {
            Role role = null;
            if ("대표".equals(employeeDTO.getPosition())) {
                role = Role.ROLE_ADMIN;
            } else if ("교육부".equals(employeeDTO.getDepartment())) {
                role = Role.ROLE_USER;
            } else if ("행정부".equals(employeeDTO.getDepartment()) || "운행부".equals(employeeDTO.getDepartment())) {
                role = Role.ROLE_MANAGER;
            } else if ("학부모".equals(employeeDTO.getDepartment())) {
                role = Role.ROLE_PARENT;
            }

            Member member = Member.builder()
                    .email(memberDTO.getEmail())
                    .name(memberDTO.getName())
                    .password(passwordEncoder.encode(memberDTO.getPassword()))
                    .address(memberDTO.getAddress())
                    .phone(memberDTO.getPhone())
                    .profileImage("Default-profile.png")
                    .role(role)
                    .build();
            member = memberService.saveMember(member);

            Employee employee = Employee.createEmployee(employeeDTO, member);

            employeeService.saveEmployee(employee);
        }  catch (Exception e) {
            model.addAttribute("errorMessage", "서버 오류가 발생했습니다.");
            return "/employee/employee_reg";
        }

        return "redirect:/admin/dashboard";
    }

    // 2. 직원리스트
    @GetMapping("/employeeList")
    public String listEmployees(Model model) {

        MultiDTO multiDTO = new MultiDTO();
        multiDTO.setMemberDTO(new MemberDTO());
        multiDTO.setEmployeeDTO(new EmployeeDTO());

        List<MultiDTO> DTO = memberService.getAllEmployeeMember();
        model.addAttribute("multiDTO", DTO);
        return "/employee/employee_list";
    }

    // 3. 마이페이지
    @GetMapping("/mypage/{id}")
    public String getEmployeeById(@PathVariable("id") Long id, @AuthenticationPrincipal PrincipalDetails principalDetails, Model model) {
        // 직원 인적사항
        MultiDTO multiDTO = memberService.readMember(id);
        // 직원 자격증리스트
        List<CertificateDTO> certificates = certificateService.getCertificatesByEmployee(principalDetails.getMember());
        // 직원 근태현황
        List<AttendanceDTO> records = attendanceService.getMonthlyAttendance(principalDetails.getMember());
        // 직원 휴가현황
        List<LeaveDTO> leaves = leaveService.getLeavesByEmployee(principalDetails.getMember());
        // 교육이력현황
        List<EducationDTO> history = educationService.getEducationHistory(principalDetails.getMember());


        model.addAttribute("multiDTO", multiDTO); // 직원정보
        model.addAttribute("certificates", certificates); // 자격증정보
        model.addAttribute("records", records); // 근태정보
        model.addAttribute("leaves", leaves); // 휴가정보
        model.addAttribute("history", history);
        // id에 해당하는 직원 정보 반환
        return "employee/mypage";
    }

    // 4. 정보수정
    @PostMapping("/update/{field}")
    public ResponseEntity<Map<String, Object>> editEmployee(
            @PathVariable("field") String field,
            @RequestBody Map<String, Object> requestData,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        // 현재 로그인한 사용자의 권한 확인
        boolean isAdmin = principalDetails.getMember().getRole() == Role.ROLE_ADMIN;

        // employeeDTO 생성
        MultiDTO multiDTO = memberService.readMember(principalDetails.getMember().getId());

        // field에 따라 동적으로 처리 (if문 내에서 switch문 사용)
        if (field != null) {
            switch (field) {
                case "name":
                    multiDTO.getMemberDTO().setName((String) requestData.get("value"));
                    break;
                case "email":
                    multiDTO.getMemberDTO().setEmail((String) requestData.get("value"));
                    break;
                case "password":
                    multiDTO.getMemberDTO().setPassword((String) requestData.get("value"));
                    break;
                case "phone":
                    multiDTO.getMemberDTO().setPhone((String) requestData.get("value"));
                    break;
                case "salary":
                    multiDTO.getEmployeeDTO().setSalary((BigDecimal) requestData.get("value"));
                    break;
                case "position":
                    multiDTO.getEmployeeDTO().setPosition((String) requestData.get("value"));
                    break;
                case "department":
                    multiDTO.getEmployeeDTO().setDepartment((String) requestData.get("value"));
                    break;
                case "status":
                    multiDTO.getEmployeeDTO().setStatus((String) requestData.get("value"));
                    break;
                case "address":
                    multiDTO.getMemberDTO().setAddress((String) requestData.get("value"));
                    break;
                // 다른 필드 추가 가능
                default:
                    throw new IllegalArgumentException("지원하지 않는 필드입니다.");
            }
        }

        // 업데이트 처리
        employeeService.updateEmployee(multiDTO);

        // 성공 응답 반환
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "업데이트 성공");
        return ResponseEntity.ok(response);
    }

    // 4-1. 직원정보수정
    @GetMapping("/edit/{id}")
    public String updateEmployeeAdmin(@PathVariable Long id, Model model){
        // 직원 인적사항
        MultiDTO multiDTO = memberService.readMember(id);
        model.addAttribute("multiDTO", multiDTO);
        return "/employee/employee_edit";
    }

    // 직원 수정 처리
    @PostMapping("/edit")
    public String updateEmployee(@ModelAttribute MultiDTO multiDTO, Model model) {
        try {

            multiDTO.getEmployeeDTO().setId(multiDTO.getEmployeeDTO().getId());
            multiDTO.getEmployeeDTO().setSalary(multiDTO.getEmployeeDTO().getSalary());
            multiDTO.getEmployeeDTO().setPosition(multiDTO.getEmployeeDTO().getPosition());
            multiDTO.getEmployeeDTO().setDepartment(multiDTO.getEmployeeDTO().getDepartment());
            multiDTO.getEmployeeDTO().setStatus(multiDTO.getEmployeeDTO().getStatus());
            multiDTO.getEmployeeDTO().setHireDate(multiDTO.getEmployeeDTO().getHireDate());
            multiDTO.getMemberDTO().setPassword(multiDTO.getMemberDTO().getPassword());
            multiDTO.getMemberDTO().setName(multiDTO.getMemberDTO().getName());
            multiDTO.getMemberDTO().setAddress(multiDTO.getMemberDTO().getAddress());
            multiDTO.getMemberDTO().setPhone(multiDTO.getMemberDTO().getPhone());
            multiDTO.getMemberDTO().setEmail(multiDTO.getMemberDTO().getEmail());

            employeeService.updateEmployee(multiDTO);

            // 수정 성공 메시지 추가
            model.addAttribute("successMessage", "직원 정보가 성공적으로 수정되었습니다.");

            return "redirect:/employee/employeeList";  // 수정된 직원 정보를 다시 수정 페이지에서 보여줌
        } catch (Exception e) {
            // 오류 메시지 처리
            model.addAttribute("errorMessage", "직원 정보를 수정하는 중 오류가 발생했습니다.");
            return "/employee/employee_edit"; // 수정 페이지로 다시 돌아감
        }
    }

    // 4-2. 프로필 이미지 업데이트
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

    // 4-3. 프로필 이미지 조회
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

    // 4-3. 프로필 이미지 삭제
//    @PostMapping("/delete-profile-image")
//    @ResponseBody
//    public ResponseEntity<Map<String, Object>> deleteProfileImage(
//            @AuthenticationPrincipal PrincipalDetails principalDetails) {
//
//        Map<String, Object> response = new HashMap<>();
//        try {
//            employeeService.deleteProfileImage(principalDetails.getEmployee().getId());
//            response.put("success", true);
//            response.put("message", "프로필 이미지가 삭제되었습니다.");
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            response.put("success", false);
//            response.put("message", e.getMessage());
//            return ResponseEntity.badRequest().body(response);
//        }
//    }

    // 이메일 중복 검사
    @GetMapping("/check-email")
    @ResponseBody
    public Map<String, Object> checkEmail(@RequestParam("email") String email) {
        Map<String, Object> response = new HashMap<>();
        boolean exists = memberService.isEmailExists(email);
        response.put("exists", exists);
        return response;
    }

    // 전화번호 중복 검사
    @GetMapping("/check-phone")
    @ResponseBody
    public Map<String, Object> checkPhone(@RequestParam("phone") String phone) {
        Map<String, Object> response = new HashMap<>();
        boolean exists = memberService.isPhoneExists(phone);
        response.put("exists", exists);
        return response;
    }

}
