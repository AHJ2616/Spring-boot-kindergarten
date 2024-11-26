package com.kinder.kindergarten.config;

import com.kinder.kindergarten.service.MemberService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
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
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig{

  // ahj2616 2024 11 19 추가
  private final MemberService memberService;
  private final CustomAuthenticationSuccessHandler successHandler;

  private final DataSource dataSource;

  // ahj2616 2024 11 19 추가
  public SecurityConfig(
          @Lazy MemberService memberService,
          @Lazy CustomAuthenticationSuccessHandler successHandler,
          DataSource dataSource) {
    this.memberService = memberService;
    this.successHandler = successHandler;
    this.dataSource = dataSource;
  }
  
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
    http
            // CSRF 보호 활성화 및 쿠키 설정
            .csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
            .authorizeHttpRequests(auth -> auth
                    // 정적 리소스 접근 허용
                    .requestMatchers("/css/**", "/js/**", "/images/**", "/image/**").permitAll()
                    // 공개 페이지 접근 허용
                    .requestMatchers("/", "/calendar","/events","/main/login", "/employee/new", "/error").permitAll()
                    // 게시판 조회 허용
                    .requestMatchers(HttpMethod.GET, "/board/get/**","/board/list/**").permitAll()
                    
                    // 역할별 접근 제한
                    .requestMatchers("/admin/**").hasRole("ADMIN")
                    .requestMatchers("/manager/**").hasRole("MANAGER")
                    .requestMatchers("/teacher/**").hasRole("USER")
                    .requestMatchers("/employee/**").hasAnyRole("ADMIN", "MANAGER", "USER")
                    .requestMatchers("/parent/**").hasRole("PARENT")
                    .requestMatchers("/erp/**").hasAnyRole("ADMIN", "MANAGER")
                    .requestMatchers("/money/**").hasAnyRole("ADMIN", "MANAGER", "USER")
                    .requestMatchers("/material/**").hasAnyRole("ADMIN", "MANAGER", "USER")
                    .requestMatchers("/board/write").hasAnyRole("ADMIN", "MANAGER", "USER","PARENT")
                    // 나머지 요청은 인증 필요
                    .anyRequest().authenticated()
            )
            .formLogin(form -> form
                    .loginPage("/main/login") // 로그인 페이지
                    .successHandler(successHandler) // 커스텀 로그인 성공 핸들러 설정
                    .usernameParameter("email")   // 로그인 키 값
                    .failureUrl("/login/error") // 로그인 실패시 URL
                    .permitAll()
            )
            .logout(logout -> logout
                    .logoutRequestMatcher(new AntPathRequestMatcher("/main/logout")) // 로그아웃 경로
                    .logoutSuccessUrl("/main/login")      // 로그아웃 성공시 URL
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID", "remember-me")
            )
            // 세션 관리 설정
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                    .maximumSessions(1) // 동시 세션 제한
                    .maxSessionsPreventsLogin(true) // 중복 로그인 차단
                    .expiredUrl("/main/login") // 세션 만료시 리다이렉트
                    .sessionRegistry(sessionRegistry())
            )
            // Remember-me 설정
            .rememberMe(remember -> remember
                    .rememberMeParameter("remember-me")
                    .tokenValiditySeconds(1800) // 30분
                    .tokenRepository(persistentTokenRepository())
                    .userDetailsService(memberService)
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
    provider.setUserDetailsService(memberService); // UserDetailsService 설정
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
