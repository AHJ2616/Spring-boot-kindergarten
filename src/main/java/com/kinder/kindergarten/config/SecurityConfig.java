package com.kinder.kindergarten.config;

import com.kinder.kindergarten.service.Employee.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {


  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
            .csrf(csrf -> csrf.ignoringRequestMatchers("/calendar","/events/**"))
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/css/**", "/js/**", "/img/**").permitAll()
                    .requestMatchers("/**", "/board/**", "/item/**", "/images/**").permitAll()
                    .requestMatchers("/erp/**").hasAnyRole("ADMIN", "MANAGER")  // ERP 접근 권한 추가
                    .requestMatchers("/admin/**").hasRole("ADMIN")
                    .anyRequest()
                    .authenticated()
            ).formLogin(form -> form
                    .loginPage("/employee/login")
                    .defaultSuccessUrl("/")
                    .usernameParameter("email")
                    .failureUrl("/parents/login/error")
                    .failureHandler(new CustomAuthenticationFailureHandler())
            ).logout(logout -> logout
                    .logoutRequestMatcher(new AntPathRequestMatcher("/employee/logout"))
                    .logoutSuccessUrl("/")
            )
            .csrf(csrf -> csrf.disable())  // CSRF 비활성화 (개발 중에만)
            .build();
  }

  private final EmployeeService employeeService;
  @Bean
  public AuthenticationManager authManager(HttpSecurity http) throws Exception {
    // HttpSecurity 에서 AuthenticationManagerBuilder 를 가져옴
    AuthenticationManagerBuilder authenticationManagerBuilder =
            http.getSharedObject(AuthenticationManagerBuilder.class);
    // DaoAuthenticationProvider 를 사용하여 사용자 인증 제공자를 설정
    authenticationManagerBuilder.authenticationProvider(daoAuthenticationProvider());
    // 구성된 AuthenticationManager 를 반환
    return authenticationManagerBuilder.build();
  }

  @Bean
  public DaoAuthenticationProvider daoAuthenticationProvider() {
    // DaoAuthenticationProvider 인스턴스를 생성
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    // employeeService 를 설정하여 사용자 정보를 가져오는 방법을 정의
    provider.setUserDetailsService(employeeService); // UserDetailsService 설정
    // 비밀번호를 검증할 때 사용할 비밀번호 인코더를 설정
    provider.setPasswordEncoder(passwordEncoder()); // 비밀번호 암호화기 설정
    return provider;
  }



  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

}