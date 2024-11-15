package com.kinder.kindergarten.entity;


import com.kinder.kindergarten.DTO.MemberDTO;
import com.kinder.kindergarten.constant.employee.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@Table(name = "Member")
@Entity
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id
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

    // 2차 합본 합치면서 추가 - 2024 11 13
    @Builder.Default
    @OneToMany(mappedBy = "member",orphanRemoval = true)
    private List<FcmTokenEntity> fcmTokens = new ArrayList<>();

    public static Member ceateMember(MemberDTO memberDTO, PasswordEncoder passwordEncoder){

        return Member.builder()
                .name(memberDTO.getName())
                .email(memberDTO.getEmail())
                .password(passwordEncoder.encode(memberDTO.getPassword()))
                .address(memberDTO.getAddress())
                .phone(memberDTO.getPhone())
                .build();
    }

}
