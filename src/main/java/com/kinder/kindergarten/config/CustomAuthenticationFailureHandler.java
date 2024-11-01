package com.kinder.kindergarten.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
  @Override
  public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
    // response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    // 로그인 실패 시 처리할 로직
    String errorMessage = "아이디 또는 비밀번호를 확인해주세요.";
    request.getSession().setAttribute("loginErrorMsg", errorMessage);
    System.err.println("로그인 실패: " + exception.getMessage());
    // 로그인 페이지로 리다이렉트
    response.sendRedirect("/employee/login?error=true");
  }


}
