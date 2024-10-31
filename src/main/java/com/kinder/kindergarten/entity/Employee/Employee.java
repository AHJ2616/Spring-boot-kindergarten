package com.kinder.kindergarten.entity.Employee;

import com.kinder.kindergarten.constant.Employee.Position;
import com.kinder.kindergarten.constant.Employee.Role;
import com.kinder.kindergarten.DTO.Employee.EmployeeDTO;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


@Table(name = "Employee")
@Entity
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    @Id
    @Column(name = "employee_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 직원 기본키

    @Column(name = "employee_cleanup", nullable = false, unique = true)
    private String cleanup; // 직원 사번

    @Column(name = "employee_name", nullable = false)
    private String name; // 직원 이름

    @Column(name = "employee_email", nullable = false, unique = true)
    private String email; // 직원 이메일

    @Column(name = "employee_password", nullable = false)
    private String password; // 직원 비밀번호

    @Column(name = "employee_phone", nullable = false, unique = true)
    private String phone; // 직원 연락처

    @Column(name = "employee_position")
    private String position; // 직원 직위

    @Column(name = "employee_department")
    private String department; // 직원 부서

    @Column(name = "employee_status")
    private String status; // 재직상태

    @Column(name = "employee_salary", nullable = false)
    private BigDecimal salary; // 직원 급여

    @Column(name = "employee_hireDate")
    private LocalDate hireDate; // 입사날짜

    @Column(name = "employee_annual_leave")
    private double annualLeave; // 연차 잔여일수

    @Column(name = "employee_position_level")
    private Integer positionLevel; // 직급 레벨 (결재선용)

    @Column(name = "employee_role")
    @Enumerated(EnumType.STRING)
    private Role role = Role.ROLE_MANAGER; // 기본값은 직원

    @OneToMany(mappedBy = "manager", cascade = CascadeType.ALL)
    private List<Project> projects = new ArrayList<>(); // 프로젝트 목록

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<Evaluation> evaluations = new ArrayList<>(); // 인사평가 목록

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<Approval> approvals = new ArrayList<>(); // 결재 문서 목록

    // 사번의 자동번호 생성자
    public static String generateAutoNumber() {
        long currentTimeMillis = System.currentTimeMillis();
        return String.valueOf(currentTimeMillis).substring(5); // 현재 시간을 기반으로 8자리
    }

    public static Employee createEmployee(EmployeeDTO employeeDTO, PasswordEncoder passwordEncoder) {
        // 사번 작성
        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String timeNum = generateAutoNumber().substring(5);
        String cleanup = currentDate + timeNum;
        // 직급별 연차 지정 ENUM
        Position pos = Position.valueOf(employeeDTO.getPosition().toUpperCase());

        return Employee.builder()
                .cleanup(cleanup)
                .name(employeeDTO.getName())
                .email(employeeDTO.getEmail())
                .password(passwordEncoder.encode(employeeDTO.getPassword()))
                .phone(employeeDTO.getPhone())
                .position(employeeDTO.getPosition())
                .department(employeeDTO.getDepartment())
                .status(employeeDTO.getStatus())
                .annualLeave(pos.getAnnualLeave())
                .salary(employeeDTO.getSalary())
                .hireDate(employeeDTO.getHireDate())
                .build();
    }

}
