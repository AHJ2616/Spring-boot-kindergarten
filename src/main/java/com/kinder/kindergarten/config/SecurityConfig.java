package com.kinder.kindergarten.config;

import com.kinder.kindergarten.service.employee.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig{

  private final EmployeeService employeeService;
  private final CustomAuthenticationSuccessHandler successHandler;

  private final DataSource dataSource;
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
    http
            // CSRF 보호 활성화 및 쿠키 설정
            .csrf(csrf -> csrf
                    .ignoringRequestMatchers("/calendar", "/events/**")  // calendar와 events 관련 엔드포인트는 CSRF 검사 제외
                    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))


            .authorizeHttpRequests(auth -> auth
                    // css,js,image 파일 접근 허용
                    .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                    // 로그인페이지 허용
                    .requestMatchers("/main/login", "/employee/new").permitAll()
                    .requestMatchers("/erp/**").permitAll()

                    // 본인 작업 경로 적어주시면 됩니다.
                    .requestMatchers("/admin/**").hasRole("ADMIN") // 관리자만 /admin/** 경로 접근 가능
                    .requestMatchers("/manager/**").hasRole("MANAGER") // 매니저만 /manager/** 경로 접근 가능
                    .requestMatchers("/teacher/**").hasRole("USER") // 사용자만 /teacher/** 경로 접근 가능
                    .requestMatchers("/employee/**").hasAnyRole("ADMIN", "MANAGER", "USER") // 직원은 모든 역할 접근 가능
                    .requestMatchers("/parent/**").hasRole("Parent")
                    .requestMatchers("/consent", "/consent/**").permitAll()
                    .requestMatchers("/erp/**").hasAnyRole("ADMIN", "MANAGER")  // ERP 접근 권한 추가
                    .requestMatchers("/money/**").hasAnyRole("ADMIN", "MANAGER", "USER") // 2024 11 11 회계 관리 추가
                    .requestMatchers("/material/**").hasAnyRole("ADMIN", "MANAGER", "USER") // 2024 11 11 자재 관리 추가
                    .requestMatchers("/**", "/board/**", "/item/**", "/images/**").permitAll()
                    .anyRequest().authenticated()
            )
            .formLogin(form -> form
                    .loginPage("/main/login") // 로그인 페이지
                    .successHandler(successHandler) // 커스텀 로그인 성공 핸들러 설정
                    .usernameParameter("email")   // 로그인 키 값
                    .failureUrl("/login/error") // 로그인 실패시 URL
                    .failureUrl("/parents/login/error") // 로그인 실패시 URL 학부모/원아 부분 2024 11 13
                    .permitAll()
            )
            .logout(logout -> logout
                    .logoutRequestMatcher(new AntPathRequestMatcher("/main/logout")) // 로그아웃 경로
                    .logoutSuccessUrl("/main/login")      // 로그아웃 성공시 URL
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID", "remember-me")
            )

/*            .csrf(csrf -> csrf.disable())  // CSRF 비활성화 (개발 중에만)
            .build();   사용법 몰라서 일단 잠가 놓았습니다. 2024 11 13*/

            // 세션 관리 설정
            .sessionManagement(session -> session
                    .maximumSessions(1) // 동시 세션 제한
                    .maxSessionsPreventsLogin(true) // 중복 로그인 차단
                    .expiredUrl("/main/login") // 세션 만료시 리다이렉트
                    .sessionRegistry(sessionRegistry())
            )
            // 세션 설정
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                    .invalidSessionUrl("/main/login?invalid")
                    .sessionFixation().migrateSession()
                    .maximumSessions(1)
                    .maxSessionsPreventsLogin(true)
            )
            // Remember-me 설정
            .rememberMe(remember -> remember
                    .rememberMeParameter("remember-me")
                    .tokenValiditySeconds(1800) // 30분
                    .tokenRepository(persistentTokenRepository())
                    .userDetailsService(employeeService)
                    .key("uniqueAndSecret") // Remember-me 토큰 암호화 키
            );





    return http.build();
  }

  // AuthenticationManager 설정
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

  // DaoAuthenticationProvider 설정
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

  // 세션 레지스트리 설정
  @Bean
  public SessionRegistry sessionRegistry() {
    return new SessionRegistryImpl();
  }

  // Remember-me 토큰 저장소 설정
  @Bean
  public PersistentTokenRepository persistentTokenRepository() {
    JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
    tokenRepository.setDataSource(dataSource);
    return tokenRepository;
  }

  // 비밀번호 인코더 설정
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
