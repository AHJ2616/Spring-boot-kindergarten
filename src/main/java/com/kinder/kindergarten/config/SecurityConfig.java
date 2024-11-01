package com.kinder.kindergarten.config;

import com.kinder.kindergarten.service.Employee.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig{

    private final EmployeeService employeeService;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/", "/employee/new", "/employee/login").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/employee/login") // 로그인 페이지
                        .defaultSuccessUrl("/")       // 로그인 성공시 URL
                        .usernameParameter("email")   // 로그인 키 값
                        .failureUrl("/employee/login/error") // 로그인 실패시 URL
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/employee/logout")) // 로그아웃 경로
                        .logoutSuccessUrl("/employee/login")      // 로그아웃 성공시 URL
                        .invalidateHttpSession(true)
                );

        return http.build();
    }

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

    @Bean  // 패스워드를 db에 저장할 때 암호화 처리함.
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
