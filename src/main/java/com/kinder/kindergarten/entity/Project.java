package com.kinder.kindergarten.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "project")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id") // 프로젝트 매니저로 설정된 직원
    private Employee manager;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    @Builder.Default
    private List<ProjectMember> members = new ArrayList<>();

    // 프로젝트 멤버 추가 메서드
    public void addMember(Employee employee, String role) {
        ProjectMember member = ProjectMember.builder()
                .project(this)
                .employee(employee)
                .role(role)
                .joinDate(LocalDateTime.now())
                .isActive(true)
                .build();
        this.members.add(member);
    }
}
