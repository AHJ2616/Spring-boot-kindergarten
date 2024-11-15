package com.kinder.kindergarten.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MemberDTO {

    private String name;    // 이름
    private String email;   // 이메일
    private String password;// 비밀번호
    private String address; // 주소
    private String phone;   // 전화번호

    private String role; // 2차 합본 하면서 추가 - 2024 11 13
}
