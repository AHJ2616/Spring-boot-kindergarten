package com.kinder.kindergarten.entity.employee;

import com.kinder.kindergarten.entity.employee.Employee;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Table(name = "Education")
@Entity
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Education {

    @Id
    @Column(name = "education_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 교육이력 기본키

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee; // 직원 기본키

    @Column(name = "education_name", nullable = false)
    private String name; // 교육이름

    @Column(name = "education_startDate")
    private LocalDate startDate; // 교육시작 날짜

    @Column(name = "education_endDate")
    private LocalDate endDate; // 교육종료 날짜

    @Column(name = "education_certificate")
    private String certificate; // 수료증 여부


}
