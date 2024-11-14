package com.kinder.kindergarten.DTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MemberDTO {

  private String email;

  private String password;

  private String name;

  private String address;

  private String phone;

  private String role;
}
