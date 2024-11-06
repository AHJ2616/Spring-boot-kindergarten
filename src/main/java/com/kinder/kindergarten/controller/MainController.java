package com.kinder.kindergarten.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Log4j2
@Controller
public class MainController {

  @GetMapping(value={"/main","/"})
    String main(){
    return "main";
    }

    @GetMapping(value = "/member/login")
  String login(){
    return "/member/login";
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

  @GetMapping("/admin/dashboard")
  @PreAuthorize("hasRole('ADMIN')")
  public String adminDashboard(Model model) {
    // 원장용 대시보드
    return "employee/admin";
  }

  @GetMapping("/manager/dashboard")
  @PreAuthorize("hasRole('MANAGER')")
  public String managerDashboard(Model model) {
    // 직원용 대시보드
    return "employee/manager";
  }

  @GetMapping("/teacher/dashboard")
  @PreAuthorize("hasRole('USER')")
  public String teacherDashboard(Model model) {
    return "employee/user";
  }

  @GetMapping("/parent/dashboard")
  @PreAuthorize("hasRole('Parent')")
  public String parentDashboard(Model model) {
    // 학부모용 대시보드
    return "dashboard/parent";
  }

}
