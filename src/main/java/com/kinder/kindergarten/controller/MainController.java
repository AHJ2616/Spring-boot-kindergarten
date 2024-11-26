package com.kinder.kindergarten.controller;

import com.kinder.kindergarten.DTO.MemberDTO;
import com.kinder.kindergarten.DTO.board.BoardDTO;
import com.kinder.kindergarten.annotation.CurrentUser;
import com.kinder.kindergarten.config.PrincipalDetails;
import com.kinder.kindergarten.service.board.BoardService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Log4j2
@Controller
@RequiredArgsConstructor
public class MainController {

    private final BoardService boardService;

  @GetMapping(value={"/main","/"})
  public String main(Model model) {
    // NOTIFICATION과 ABSOLUTE 타입의 최신 게시글 4개 조회
    Page<BoardDTO> notices = boardService.getLatestNotices(PageRequest.of(0, 4));
    model.addAttribute("notices", notices.getContent());
    return "main";
  }


  //캘린더
  @GetMapping(value = "/calendar")
  public String showCalendar(@CurrentUser PrincipalDetails principalDetails, Model model) {
    String userRole = "ROLE_NONE";
    
    if (principalDetails != null && principalDetails.getMember() != null) {
        userRole = principalDetails.getMember().getRole().toString();
    }
    
    model.addAttribute("userRole", userRole);
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
