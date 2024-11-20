package com.kinder.kindergarten.controller;

import com.kinder.kindergarten.DTO.MemberDTO;
import com.kinder.kindergarten.annotation.CurrentUser;
import com.kinder.kindergarten.config.PrincipalDetails;
import com.kinder.kindergarten.service.employee.EmployeeService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@Controller
@RequiredArgsConstructor
public class MainController {
  private final EmployeeService employeeService;
  @GetMapping(value={"/main","/"})
  String main(){
    return "main";
  }

  //캘린더
  @GetMapping(value = "/calendar")
  public String showCalendar(@CurrentUser PrincipalDetails principalDetails, Model model){
    String userRole = principalDetails.getMember().getRole().toString();
    model.addAttribute("userRole",userRole);
    return "calendar";
  }
  @GetMapping(value = "/main/login")
  public String loginMember(String email) {return "/login";}

  @GetMapping(value = "/login/error")
  public String loginError(Model model){
    model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요");
    return "/login";
  }

  @GetMapping("/admin/dashboard")
  @PreAuthorize("hasRole('ADMIN')")
  public String adminDashboard(Model model) {
    // 원장용 대시보드
    return "dashBoard/admin";
  }

  @GetMapping("/manager/dashboard")
  @PreAuthorize("hasRole('MANAGER')")
  public String managerDashboard(Model model) {
    // 직원용 대시보드
    return "dashBoard/manager";
  }

  @GetMapping("/teacher/dashboard")
  @PreAuthorize("hasRole('USER')")
  public String teacherDashboard(Model model) {
    return "dashBoard/user";
  }

  @PostMapping("/test/register")
  public void resisterMember(@RequestBody MemberDTO memberDTO) {
    //회원가입
  }


}


