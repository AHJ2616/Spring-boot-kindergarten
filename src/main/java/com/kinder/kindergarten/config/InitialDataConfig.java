package com.kinder.kindergarten.config;

import com.kinder.kindergarten.constant.employee.Role;
import com.kinder.kindergarten.constant.employee.Status;
import com.kinder.kindergarten.entity.employee.Employee;
import com.kinder.kindergarten.service.employee.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class InitialDataConfig {

    private final EmployeeService employeeService;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initAdminAccount() {
        return args -> {
            try {
                // 관리자 계정이 없을 경우에만 생성
                if (!employeeService.isEmailExists("admin1@admin.com")) {
                    Employee admin = Employee.builder()
                            .email("admin1@admin.com")
                            .password(passwordEncoder.encode("1234"))
                            .name("관리자")
                            .phone("010-1234-5679")
                            .role(Role.ROLE_ADMIN)
                            .build();

                    employeeService.saveEmployee(admin);

                    System.out.println("관리자 계정이 생성되었습니다.");
                    System.out.println("Email: admin1@admin.com");
                    System.out.println("Password: 1234");
                }
            } catch (Exception e) {
                System.err.println("관리자 계정 생성 중 오류 발생: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }
}
