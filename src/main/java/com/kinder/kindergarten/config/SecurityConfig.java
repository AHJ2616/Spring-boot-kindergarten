package com.kinder.kindergarten.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true) // 시큐리티 6에서 변경 https://jake-seo-dev.tistory.com/82
// @EnableWebSecurity 시큐리티 5버전 까지만 활용
public class SecurityConfig {

    @Bean  // 시큐리트 6에서 config(HttpSecurity http) 변경됨
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
      return http.authorizeHttpRequests(authorizeHttpRequestsCustomizer -> authorizeHttpRequestsCustomizer
                      .requestMatchers("/css/**", "/js/**", "/img/**").permitAll()
                      .requestMatchers("/**", "/board/**", "/item/**", "/images/**", "/parents/**"
                      ).permitAll()
                      .requestMatchers("/admin/**").hasRole("ADMIN")  //관리자 권한 접속 범위 설정
                      //.requestMatchers("/board/delete/**").hasRole("USER") //유저 권한으로 삭제 가능하게
                      .anyRequest()
                      .authenticated()
              ) .formLogin(form -> {
                form
                        .loginPage("/parents/login")    // 로그인 페이지
                        .defaultSuccessUrl("/")         // 로그인 성공시 기본 경로
                        .usernameParameter("email")     // 로그인시 인증 키값
                        .failureUrl("/parents/login/error");  // 로그인 실패시 갈 경로
              })
              .logout(logout -> {                         // 로그아웃용
                        logout.logoutRequestMatcher(new AntPathRequestMatcher("/parents/logout")) // 로그아웃 처리용 경로
                                .logoutSuccessUrl("/");     // 로그아웃 성공시 갈 경로
                      }

              )
              .build()
              ;  }


    @Bean  // 패스워드를 db에 저장할 때 암호화 처리함.
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
