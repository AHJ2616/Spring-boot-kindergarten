package com.kinder.kindergarten.entity;


import com.kinder.kindergarten.DTO.MemberDTO;
import com.kinder.kindergarten.DTO.employee.EmployeeDTO;
import com.kinder.kindergarten.constant.employee.Role;
import com.kinder.kindergarten.entity.employee.Employee;
import com.kinder.kindergarten.entity.employee.Employee_File;
import com.kinder.kindergarten.entity.employee.Leave;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Table(name = "Member")
@Entity
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member extends TimeEntity{

    @Id
    @Column(name="member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_profile")
    private String profileImage;

    @Column(name = "member_email")
    private String email;

    @Column(name = "member_password")
    private String password;

    @Column(name = "member_name")
    private String name;

    @Column(name = "member_address")
    private String address;

    @Column(name = "member_phone")
    private String phone;

    @Column(name = "member_role")
    @Enumerated(EnumType.STRING)
    private Role role;

    // member 필드 기준 연관관계
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Employee> employees = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Employee_File> files;

    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Leave> leaves = new ArrayList<>();

    // 2차 합본 합치면서 추가 - 2024 11 13
    @Builder.Default
    @OneToMany(mappedBy = "member",orphanRemoval = true)
    private List<FcmTokenEntity> fcmTokens = new ArrayList<>();

}
