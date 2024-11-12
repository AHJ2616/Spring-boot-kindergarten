package com.kinder.kindergarten.entity;

import com.kinder.kindergarten.constant.employee.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "member")
public class MemberEntity {
  @Id
  @Column(name="member_email")
  private String email;

  @Column(name="member_password")
  private String password;

  @Column(name="member_name")
  private String name;

  @Column(name="member_address")
  private String address;

  @Column(name="member_phone")
  private String phone;

  @Column(name="member_role")
  @Enumerated(EnumType.STRING)
  private Role role;

  @OneToMany(mappedBy = "member",orphanRemoval = true)
  private List<FcmTokenEntity> fcmTokens = new ArrayList<>();
}
