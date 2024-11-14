package com.kinder.kindergarten.controller;

import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@Controller
public class MainController {


  @GetMapping(value={"/main","/"})
  String main(){
    return "main";
  }


  //캘린더
  @GetMapping(value = "/calendar")
  public String showCalendar(){
    return "calendar";
  }


  @GetMapping(value = "/main/login")
  public String loginMember(String email) {return "/login";}

  @GetMapping(value = "/login/error")
  public String loginError(Model model){
    model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요");
    return "/login";
  }

//  @PostMapping(value = "/member/new")
//  public String newMember(@Valid BindingResult bindingResult, Model model) {
//
//    try {
//      Member member = Member.builder().build();
//      employeeService.saveEmployee(employee);
//    }  catch (Exception e) {
//      model.addAttribute("errorMessage", "서버 오류가 발생했습니다.");
//      return "employee/new";
//    }
//
//    return "redirect:/admin/dashboard";
//  }

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

  @PostMapping("/main/api/extend-session")
  @ResponseBody
  public ResponseEntity<Map<String, Object>> extendSession(HttpSession session) {
    Map<String, Object> response = new HashMap<>();
    try {
      // 세션 만료 시간을 30분으로 설정
      session.setMaxInactiveInterval(1800);
      response.put("success", true);
      response.put("message", "세션이 연장되었습니다.");
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      response.put("success", false);
      response.put("message", "세션 연장에 실패했습니다.");
      return ResponseEntity.badRequest().body(response);
    }
  }

  @GetMapping("/main/api/check-session")
  @ResponseBody
  public ResponseEntity<Map<String, Object>> checkSession(HttpSession session) {
    Map<String, Object> response = new HashMap<>();
    response.put("timeLeft", session.getMaxInactiveInterval());
    return ResponseEntity.ok(response);
  }

}
