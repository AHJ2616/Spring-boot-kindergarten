package com.kinder.kindergarten.entity.Employee;

import com.kinder.kindergarten.entity.Employee.Employee;
import com.kinder.kindergarten.entity.Employee.Project;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "project_member")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    private String role; // 프로젝트 내 역할
    private LocalDateTime joinDate;
    private boolean isActive = true;
}
