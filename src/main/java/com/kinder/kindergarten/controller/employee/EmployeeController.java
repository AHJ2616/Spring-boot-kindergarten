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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.List;



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
            Role role;
            switch (employeeDTO.getDepartment()) {
                case "교육부":
                    role = Role.ROLE_USER;
                    break;
                case "행정부":
                case "운행부":
                    role = Role.ROLE_MANAGER;
                    break;
                case "학부모":
                    role = Role.ROLE_PARENT;
                    break;
                default:
                    role = null;
                    break;
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
            memberService.saveMember(member);

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

//    // 4. 정보수정
//    @PostMapping("/update/{field}")
//    public ResponseEntity<Map<String, Object>> editEmployee(
//            @PathVariable("field") String field,
//            @RequestBody Map<String, Object> requestData,
//            @AuthenticationPrincipal PrincipalDetails principalDetails) {
//
//        // 현재 로그인한 사용자의 권한 확인
//        boolean isAdmin = principalDetails.getMember().getRole() == Role.ROLE_ADMIN;
//
//        // employeeDTO 생성
//        MultiDTO multiDTO = memberService.readMember(principalDetails.getMember().getId());
//
//        // field에 따라 동적으로 처리 (if문 내에서 switch문 사용)
//        if (field != null) {
//            switch (field) {
//                case "name":
//                    multiDTO.setName((String) requestData.get("value"));
//                    break;
//                case "email":
//                    dto.setEmail((String) requestData.get("value"));
//                    break;
//                case "phone":
//                    dto.setPhone((String) requestData.get("value"));
//                    break;
//                case "position":
//                    dto.setPosition((String) requestData.get("value"));
//                    break;
//                case "department":
//                    dto.setDepartment((String) requestData.get("value"));
//                    break;
//                case "status":
//                    dto.setStatus((String) requestData.get("value"));
//                    break;
//                case "address":
//                    dto.setAddress((String) requestData.get("value"));
//                    break;
//                // 다른 필드 추가 가능
//                default:
//                    throw new IllegalArgumentException("지원하지 않는 필드입니다.");
//            }
//        }
//
//        // 업데이트 처리
//        employeeService.updateEmployee(dto);
//
//        // 성공 응답 반환
//        Map<String, Object> response = new HashMap<>();
//        response.put("success", true);
//        response.put("message", "업데이트 성공");
//        return ResponseEntity.ok(response);
//    }

}
