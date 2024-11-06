package com.kinder.kindergarten.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
  //ajax사용시 header에 XMLHttpRequest라는 값이 세팅되어 요청이 오는데, 인증되지 않은 사용자가
  //ajax으로 요청시, "Unauthorized" 에러를 발생시킨다
  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
    if("XMLHttpRequest".equals(request.getHeader("x-requested-wtih"))){response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Unauthorized");}else{
      response.sendRedirect("/main/login");
    }
  }
}
