package com.kinder.kindergarten.entity.employee;

import com.kinder.kindergarten.constant.employee.DayOff;
import com.kinder.kindergarten.entity.employee.Employee;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Table(name = "Employee_Leave")
@Entity
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Leave {

    @Id
    @Column(name = "leave_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 휴가 기본키

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee; // 직원 기본키

    @Column(name = "leave_start")
    private LocalDate start; // 휴가 시작 날짜

    @Column(name = "leave_end")
    private LocalDate end; // 휴가 종료 날짜

    @Column(name = "leave_type")
    @Enumerated(EnumType.STRING)
    private DayOff type; // 휴가 유형

    @Column(name = "leave_status")
    private String status; // 승인여부

    @Column(name = "leave_total")
    private double total; // 사용연차

    @Column(name = "leave_reason")
    private String le_reason; // 휴가 사유

    @Column(name = "leave_rejectReason")
    private String rejectReason; // 반려 사유

}
