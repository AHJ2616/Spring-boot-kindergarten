package com.kinder.kindergarten.DTO.parent;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParentInfoDTO {

    /*
    학부모 동의서 페이지에서 학부모가 입력하는 DTO

    11.18 -> 학부모에 관한 필드명이 겹치는 DTO들이 있는데 혹시 몰라서 남겨두었습니다.

     */
    @NotEmpty(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String parentEmail; // 학부모 이메일(계정)

    @NotEmpty(message = "이름은 필수 입력 값입니다.")
    private String parentName;  // 학부모 성함

    @NotEmpty(message = "휴대폰 번호는 필수 입력 값입니다.")
    @Pattern(regexp = "^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$",
            message = "올바른 휴대폰 번호 형식이 아닙니다.")
    private String parentPhone;// 학부모 연락처

    @Pattern(regexp = "^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$",
            message = "올바른 휴대폰 번호 형식이 아닙니다.")
    private String childrenEmergencyPhone; // 학부모 비상연락처(선택 사항)

    @NotEmpty(message = "주소는 필수 입력 값입니다.")
    private String parentAddress;// 학부모 주소

    private String detailAddress;// 학부모 상세 주소
}
