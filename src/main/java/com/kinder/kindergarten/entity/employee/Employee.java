package com.kinder.kindergarten.entity.employee;

import com.kinder.kindergarten.DTO.employee.EmployeeDTO;
import com.kinder.kindergarten.constant.employee.Position;
import com.kinder.kindergarten.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


@Table(name = "Employee")
@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id")
    private Long id; // 직원 기본키

    @Column(name = "employee_cleanup", nullable = false, unique = true)
    private String cleanup; // 직원 사번

    @Column(name = "employee_position", unique = true)
    private String position; // 직원 직위

    @Column(name = "employee_department")
    private String department; // 직원 부서

    @Column(name = "employee_status")
    private String status; // 재직상태

    @Column(name = "employee_salary", nullable = false)
    private BigDecimal salary; // 직원 급여

    @Column(name = "employee_hireDate")
    private LocalDate hireDate; // 입사 날짜

    @Column(name = "employee_annual_leave")
    private double annualLeave; // 연차 잔여일수

    @Column(name = "employee_position_level")
    private Integer positionLevel; // 직급 레벨

/*    @OneToOne
    @JoinColumn(name = "member_id", insertable = true, updatable = true)
    private Member member;*/

/*    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;*/

    @OneToOne
    @JoinColumn(name = "member_id", insertable = true, updatable = true)
    private Member member;

    // 사번 자동 번호 생성자
    public static String generateAutoNumber() {
        long currentTimeMillis = System.currentTimeMillis();
        return String.valueOf(currentTimeMillis).substring(5);
    }

    public static Employee createEmployee(EmployeeDTO employeeDTO, Member member) {
        // 사번 작성
        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String timeNum = generateAutoNumber().substring(5);
        String cleanup = currentDate + timeNum;

        Position pos = Position.valueOf(employeeDTO.getPosition().toUpperCase());

        Employee employee = new Employee();
        employee.setCleanup(cleanup);
        employee.setPosition(employeeDTO.getPosition());
        employee.setDepartment(employeeDTO.getDepartment());
        employee.setStatus(employeeDTO.getStatus());
        employee.setAnnualLeave(pos.getAnnualLeave()); // 직급별 연차 설정
        employee.setPositionLevel(pos.getPositionLevel()); // 직급 레벨 설정
        employee.setSalary(employeeDTO.getSalary());
        employee.setHireDate(employeeDTO.getHireDate());
        employee.setMember(member); // Member 객체와 관계 설정
        return employee;
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
