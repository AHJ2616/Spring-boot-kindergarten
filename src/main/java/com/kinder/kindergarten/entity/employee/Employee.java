package com.kinder.kindergarten.entity.employee;
import com.kinder.kindergarten.DTO.employee.EmployeeDTO;
import com.kinder.kindergarten.constant.employee.Position;
import com.kinder.kindergarten.constant.employee.Role;
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

    @Column(name = "employee_address", nullable = false)
    private String address; // 직원 주소
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

    @Column(name = "employee_profile_img")
    private String profileImageUrl; // 프로필 이미지 URL

    @Column(name = "employee_role")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.ROLE_MANAGER; // 기본값은 직원

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Leave> leaves = new ArrayList<>();

    // 사번의 자동번호 생성자
    public static String generateAutoNumber() {
        long currentTimeMillis = System.currentTimeMillis();
        return String.valueOf(currentTimeMillis).substring(5);
    }

    public static Employee createEmployee(EmployeeDTO employeeDTO, PasswordEncoder passwordEncoder) {
        // 사번 작성
        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        // 현재 시간을 기반으로 8자리
        String timeNum = generateAutoNumber().substring(5);
        String cleanup = currentDate + timeNum;

        String profileImageUrl = (employeeDTO.getProfileImageUrl() != null && !employeeDTO.getProfileImageUrl().isEmpty())
                ? employeeDTO.getProfileImageUrl()
                : "Default-profile.png";

        // 직급별 연차 지정 ENUM
        Position pos = Position.valueOf(employeeDTO.getPosition().toUpperCase());

        return Employee.builder()
                .cleanup(cleanup)
                .profileImageUrl(profileImageUrl)
                .name(employeeDTO.getName())
                .email(employeeDTO.getEmail())
                .password(passwordEncoder.encode(employeeDTO.getPassword()))
                .phone(employeeDTO.getPhone())
                .position(employeeDTO.getPosition())
                .department(employeeDTO.getDepartment())
                .status(employeeDTO.getStatus())
                .role(employeeDTO.getRoleByDepartment())
                .address(employeeDTO.getAddress())
                .annualLeave(pos.getAnnualLeave())
                .salary(employeeDTO.getSalary())
                .hireDate(employeeDTO.getHireDate())
                .build();
    }

    // 연차 사용
    public boolean useAnnualLeave(double days) {
        if (this.annualLeave >= days) {
            this.annualLeave -= days;
            return true;
        }
        return false;
    }

    // 연차 반환 (휴가 취소/반려 시)
    public void returnAnnualLeave(double days) {
        this.annualLeave += days;
    }

    // 연차 초기화 (매년 초기화)
    public void resetAnnualLeave() {
        Position pos = Position.valueOf(this.position.toUpperCase());
        this.annualLeave = pos.getAnnualLeave();
    }

}
