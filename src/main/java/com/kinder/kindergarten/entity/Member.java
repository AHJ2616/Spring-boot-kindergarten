package com.kinder.kindergarten.entity;


import com.kinder.kindergarten.constant.employee.Role;
import com.kinder.kindergarten.entity.employee.Employee;
import com.kinder.kindergarten.entity.employee.Employee_File;
import com.kinder.kindergarten.entity.employee.Leave;
import com.kinder.kindergarten.entity.employee.Member_File;
import jakarta.persistence.*;
import lombok.*;

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
    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Employee employees;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    private List<Member_File> files = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Leave> leaves = new ArrayList<>();


}
